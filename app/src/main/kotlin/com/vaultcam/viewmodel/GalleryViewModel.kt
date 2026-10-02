package com.vaultcam.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vaultcam.VaultCamApp
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.data.repository.VideoRepository
import com.vaultcam.service.IrysService
import com.vaultcam.service.LitProtocolService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest

class GalleryViewModel(
    private val context: Context,
    private val videoRepository: VideoRepository,
    private val litProtocolService: LitProtocolService,
    private val irysService: IrysService
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    val videos = videoRepository.videos

    fun encryptAndUpload(record: VideoRecord, ownerPublicKey: String, walletSignature: String) {
        viewModelScope.launch {
            val localPath = record.localPath ?: return@launch
            val localFile = File(localPath)
            if (!localFile.exists()) return@launch

            videoRepository.updateVideo(record.copy(status = VideoStatus.ENCRYPTING))
            _uiState.value = _uiState.value.copy(activeOperationId = record.id)

            litProtocolService.encryptFile(
                inputFile = localFile,
                ownerWalletAddress = ownerPublicKey,
                irysTransactionId = record.id
            ).onSuccess { encryptResult ->
                videoRepository.updateVideo(
                    record.copy(
                        sha256Hash = encryptResult.dataToEncryptHash,
                        isEncrypted = true,
                        status = VideoStatus.UPLOADING
                    )
                )
                irysService.uploadEncryptedVideo(
                    encryptedFile = File(encryptResult.encryptedFilePath),
                    ownerPublicKey = ownerPublicKey,
                    sha256Hash = encryptResult.dataToEncryptHash,
                    latitude = record.latitude,
                    longitude = record.longitude,
                    timestampMs = record.timestampMs,
                    walletSignature = walletSignature
                ).onSuccess { uploadResult ->
                    videoRepository.updateVideo(
                        record.copy(
                            irysTransactionId = uploadResult.transactionId,
                            ownerPublicKey = ownerPublicKey,
                            isEncrypted = true,
                            isUploaded = true,
                            status = VideoStatus.UPLOADED
                        )
                    )
                    localFile.delete()
                    File(encryptResult.encryptedFilePath).delete()
                    _uiState.value = _uiState.value.copy(
                        activeOperationId = null,
                        successMessage = "Vídeo salvo permanentemente no Irys"
                    )
                }.onFailure {
                    _uiState.value = _uiState.value.copy(
                        activeOperationId = null,
                        error = "Upload falhou: ${it.message}"
                    )
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    activeOperationId = null,
                    error = "Criptografia falhou: ${it.message}"
                )
                videoRepository.updateVideo(record.copy(status = VideoStatus.LOCAL))
            }
        }
    }

    fun deleteVideo(record: VideoRecord) {
        if (record.isLocked) {
            _uiState.value = _uiState.value.copy(error = "Vídeo bloqueado — possível evidência de incidente")
            return
        }
        viewModelScope.launch {
            videoRepository.deleteVideo(record.id)
        }
    }

    fun dismissMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = context.applicationContext as VaultCamApp
            return GalleryViewModel(
                context = context,
                videoRepository = VideoRepository(context),
                litProtocolService = app.litProtocolService,
                irysService = app.irysService
            ) as T
        }
    }
}

data class GalleryUiState(
    val activeOperationId: String? = null,
    val successMessage: String? = null,
    val error: String? = null
)
