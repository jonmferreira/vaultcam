package com.vaultcam.viewmodel

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vaultcam.VaultCamApp
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.data.repository.VideoRepository
import com.vaultcam.service.CameraService
import com.vaultcam.service.ImpactDetectorService
import com.vaultcam.service.RecordingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class RecordingViewModel(
    private val context: Context,
    private val videoRepository: VideoRepository,
    private val cameraService: CameraService,
    private val impactDetector: ImpactDetectorService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordingUiState())
    val uiState: StateFlow<RecordingUiState> = _uiState.asStateFlow()

    val recordingState = cameraService.recordingState
    val videos = videoRepository.videos

    init {
        observeImpacts()
        observeRecordingState()
    }

    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        viewModelScope.launch {
            runCatching { cameraService.bindCamera(lifecycleOwner, previewView) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun toggleRecording() {
        when (cameraService.recordingState.value) {
            is RecordingState.Recording -> cameraService.stopRecording()
            else -> {
                val outputDir = videoRepository.getVideoDir()
                cameraService.startRecording(outputDir) { file ->
                    onRecordingFinished(file)
                }
            }
        }
    }

    private fun onRecordingFinished(file: File) {
        viewModelScope.launch {
            val record = VideoRecord(
                id = UUID.randomUUID().toString(),
                localPath = file.absolutePath,
                timestampMs = System.currentTimeMillis(),
                fileSizeBytes = file.length(),
                status = VideoStatus.LOCAL
            )
            videoRepository.saveVideo(record)
            _uiState.value = _uiState.value.copy(lastSavedVideoId = record.id)
        }
    }

    private fun observeImpacts() {
        viewModelScope.launch {
            impactDetector.startListening()
            impactDetector.impacts.collectLatest { impact ->
                _uiState.value = _uiState.value.copy(
                    impactDetected = true,
                    lastImpactGForce = impact.gForce
                )
                lockRecentVideos()
            }
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
        cameraService.release()
        impactDetector.stopListening()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = context.applicationContext as VaultCamApp
            return RecordingViewModel(
                context = context,
                videoRepository = VideoRepository(context),
                cameraService = CameraService(context),
                impactDetector = ImpactDetectorService(context)
            ) as T
        }
    }
}

data class RecordingUiState(
    val isRecording: Boolean = false,
    val impactDetected: Boolean = false,
    val lastImpactGForce: Float? = null,
    val lastSavedVideoId: String? = null,
    val error: String? = null
)
