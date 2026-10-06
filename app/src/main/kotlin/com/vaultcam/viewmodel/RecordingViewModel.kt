package com.vaultcam.viewmodel

import android.content.Context
import android.location.Location
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.vaultcam.VaultCamApp
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.data.repository.VideoRepository
import com.vaultcam.service.BluetoothAlertService
import com.vaultcam.service.CameraService
import com.vaultcam.service.ImpactDetectorService
import com.vaultcam.service.IncidentAlert
import com.vaultcam.service.RecordingState
import com.vaultcam.service.WakuService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class RecordingViewModel(
    private val context: Context,
    private val videoRepository: VideoRepository,
    private val cameraService: CameraService,
    private val impactDetector: ImpactDetectorService,
    private val bluetoothAlertService: BluetoothAlertService,
    private val wakuService: WakuService
) : ViewModel() {

    private val fusedLocation = LocationServices.getFusedLocationProviderClient(context)

    private val _uiState = MutableStateFlow(RecordingUiState())
    val uiState: StateFlow<RecordingUiState> = _uiState.asStateFlow()

    val recordingState = cameraService.recordingState
    val videos = videoRepository.videos
    val currentGForce = impactDetector.currentGForce

    private var timerJob: Job? = null
    private var sosJob: Job? = null

    init {
        observeImpacts()
        observeRecordingState()
        observeGForce()
    }

    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        viewModelScope.launch {
            runCatching { cameraService.bindCamera(lifecycleOwner, previewView) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun toggleRecording() {
        when (cameraService.recordingState.value) {
            is RecordingState.Recording -> {
                cameraService.stopRecording()
                timerJob?.cancel()
                _uiState.value = _uiState.value.copy(elapsedSeconds = 0L)
            }
            else -> {
                val outputDir = videoRepository.getVideoDir()
                cameraService.startRecording(outputDir) { file ->
                    onRecordingFinished(file)
                }
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seconds = 0L
            while (true) {
                delay(1_000)
                seconds++
                _uiState.value = _uiState.value.copy(elapsedSeconds = seconds)
            }
        }
    }

    fun manualLockAndSync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            val list = videoRepository.videos.first()
            val latest = list.filter { !it.isLocked }.maxByOrNull { it.timestampMs }
            latest?.let {
                videoRepository.updateVideo(it.copy(isLocked = true))
            }
            _uiState.value = _uiState.value.copy(isSyncing = false, syncSuccess = true)
        }
    }

    fun manualAlertNetwork() {
        viewModelScope.launch {
            val location = getLastLocation()
            val alert = IncidentAlert(
                timestampMs = System.currentTimeMillis(),
                latitude = location?.latitude,
                longitude = location?.longitude,
                gForce = 0f
            )
            bluetoothAlertService.broadcastIncident(alert)
            runCatching { wakuService.publishIncident(alert) }
            lockRecentVideos()
        }
    }

    fun manualSOS() {
        manualAlertNetwork()
        sosJob?.cancel()
        sosJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSosActive = true)
            while (true) {
                val location = getLastLocation()
                if (location != null) {
                    runCatching { wakuService.publishLocation(location.latitude, location.longitude) }
                }
                delay(10_000)
            }
        }
    }

    fun stopSOS() {
        sosJob?.cancel()
        sosJob = null
        _uiState.value = _uiState.value.copy(isSosActive = false)
    }

    private fun observeGForce() {
        viewModelScope.launch {
            impactDetector.currentGForce.collectLatest { gForce ->
                _uiState.value = _uiState.value.copy(currentGForce = gForce)
            }
        }
    }

    private fun onRecordingFinished(file: File) {
        viewModelScope.launch {
            val location = getLastLocation()
            val record = VideoRecord(
                id = UUID.randomUUID().toString(),
                localPath = file.absolutePath,
                timestampMs = System.currentTimeMillis(),
                fileSizeBytes = file.length(),
                latitude = location?.latitude,
                longitude = location?.longitude,
                status = VideoStatus.LOCAL
            )
            videoRepository.saveVideo(record)
            _uiState.value = _uiState.value.copy(lastSavedVideoId = record.id)
        }
    }

    private suspend fun getLastLocation(): Location? = runCatching {
        val cts = CancellationTokenSource()
        fusedLocation.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token).await()
    }.getOrNull()

    private fun observeImpacts() {
        viewModelScope.launch {
            impactDetector.startListening()
            bluetoothAlertService.startScanningForAlerts()

            impactDetector.impacts.collectLatest { impact ->
                _uiState.value = _uiState.value.copy(
                    impactDetected = true,
                    lastImpactGForce = impact.gForce
                )
                lockRecentVideos()
                broadcastImpactAlert(impact.gForce, impact.timestampMs)
            }
        }
        viewModelScope.launch {
            bluetoothAlertService.incomingAlerts.collectLatest {
                // Alert received from nearby device — lock our recent videos too
                lockRecentVideos()
                _uiState.value = _uiState.value.copy(impactDetected = true, lastImpactGForce = it.gForce)
            }
        }
    }

    private fun broadcastImpactAlert(gForce: Float, timestampMs: Long) {
        viewModelScope.launch {
            val location = getLastLocation()
            val alert = IncidentAlert(
                timestampMs = timestampMs,
                latitude = location?.latitude,
                longitude = location?.longitude,
                gForce = gForce
            )
            bluetoothAlertService.broadcastIncident(alert)
            runCatching { wakuService.publishIncident(alert) }
        }
    }

    private fun observeRecordingState() {
        viewModelScope.launch {
            cameraService.recordingState.collectLatest { state ->
                _uiState.value = _uiState.value.copy(
                    isRecording = state is RecordingState.Recording
                )
            }
        }
    }

    private fun lockRecentVideos() {
        viewModelScope.launch {
            val list = videoRepository.videos.first()
            val twoMinutesAgo = System.currentTimeMillis() - 2 * 60 * 1000
            list.filter { it.timestampMs >= twoMinutesAgo && !it.isLocked }
                .forEach { record ->
                    videoRepository.updateVideo(record.copy(isLocked = true, status = VideoStatus.LOCKED))
                }
        }
    }

    fun dismissImpactAlert() {
        _uiState.value = _uiState.value.copy(impactDetected = false, lastImpactGForce = null)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        sosJob?.cancel()
        cameraService.release()
        impactDetector.stopListening()
        bluetoothAlertService.stopScanningForAlerts()
        bluetoothAlertService.stopBroadcast()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = context.applicationContext as VaultCamApp
            return RecordingViewModel(
                context = context,
                videoRepository = VideoRepository(context),
                cameraService = CameraService(context),
                impactDetector = ImpactDetectorService(context),
                bluetoothAlertService = app.bluetoothAlertService,
                wakuService = app.wakuService
            ) as T
        }
    }
}

data class RecordingUiState(
    val isRecording: Boolean = false,
    val impactDetected: Boolean = false,
    val lastImpactGForce: Float? = null,
    val lastSavedVideoId: String? = null,
    val error: String? = null,
    val elapsedSeconds: Long = 0L,
    val currentGForce: Float = 0f,
    val gpsActive: Boolean = false,
    val isSosActive: Boolean = false,
    val isSyncing: Boolean = false,
    val syncSuccess: Boolean = false
)
