package com.vaultcam.service

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class CameraService(private val context: Context) {

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null

    suspend fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    ) {
        cameraProvider = suspendCancellableCoroutine { cont ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                cont.resume(future.get())
            }, ContextCompat.getMainExecutor(context))
        }

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HD))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            videoCapture
        )
    }

    fun startRecording(outputDir: File, onFinished: (File) -> Unit): String {
        val vc = videoCapture ?: return ""
        val id = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val outputFile = File(outputDir, "VaultCam_$id.mp4")

        val outputOptions = FileOutputOptions.Builder(outputFile).build()

        activeRecording = vc.output
            .prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Start ->
                        _recordingState.value = RecordingState.Recording(outputFile.absolutePath)

                    is VideoRecordEvent.Finalize -> {
                        if (event.hasError()) {
                            Log.e("CameraService", "Recording error: ${event.error}")
                            _recordingState.value = RecordingState.Error("Recording error: ${event.error}")
                        } else {
                            _recordingState.value = RecordingState.Finished(outputFile.absolutePath)
                            onFinished(outputFile)
                        }
                    }
                }
            }
        return id
    }

    fun stopRecording() {
        activeRecording?.stop()
        activeRecording = null
    }

    fun release() {
        stopRecording()
        cameraProvider?.unbindAll()
        _recordingState.value = RecordingState.Idle
    }
}

sealed class RecordingState {
    data object Idle : RecordingState()
    data class Recording(val filePath: String) : RecordingState()
    data class Finished(val filePath: String) : RecordingState()
    data class Error(val message: String) : RecordingState()
}
