package com.vaultcam.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vaultcam.VaultCamApp
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.data.repository.CreditRepository
import com.vaultcam.data.repository.VideoRepository
import com.vaultcam.service.IrysService
import com.vaultcam.service.LitProtocolService
import com.vaultcam.service.RewardService
import com.vaultcam.service.WalletService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class GalleryViewModel(
    private val context: Context,
    private val videoRepository: VideoRepository,
    private val litProtocolService: LitProtocolService,
    private val irysService: IrysService,
    private val walletService: WalletService,
    private val creditRepository: CreditRepository,
    private val rewardService: RewardService
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    val videos = videoRepository.videos
    val credits = creditRepository.balance
    val pendingSkr = rewardService.pendingSkr

    fun encryptAndUpload(record: VideoRecord, activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(activeOperationId = record.id)
            encryptAndUploadInternal(record, activity)
        }
    }

    fun shareWithNetwork(record: VideoRecord, activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(activeOperationId = record.id)
            encryptAndUploadInternal(record, activity, isNetworkShare = true)
        }
    }

    private suspend fun encryptAndUploadInternal(
        record: VideoRecord,
        activity: ComponentActivity,
        isNetworkShare: Boolean = false
    ) {
        val localPath = record.localPath ?: run {
            _uiState.value = _uiState.value.copy(activeOperationId = null)
            return
        }
        val localFile = File(localPath)
        if (!localFile.exists()) {
            _uiState.value = _uiState.value.copy(activeOperationId = null)
            return
        }

        val publicKey = walletService.getConnectedAddress()
            ?: walletService.connect(activity).getOrElse {
                _uiState.value = _uiState.value.copy(
                    activeOperationId = null,
                    error = "Wallet: ${it.message}"
                )
                return
            }

        val signature = walletService.signMessage(
            activity,
            "VaultCam upload ${record.id}"
        ).getOrElse {
            _uiState.value = _uiState.value.copy(
                activeOperationId = null,
                error = "Assinatura cancelada"
            )
            return
        }

        videoRepository.updateVideo(record.copy(status = VideoStatus.ENCRYPTING))

        litProtocolService.encryptFile(
            inputFile = localFile,
            ownerWalletAddress = publicKey,
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
                ownerPublicKey = publicKey,
                sha256Hash = encryptResult.dataToEncryptHash,
                latitude = record.latitude,
                longitude = record.longitude,
                timestampMs = record.timestampMs,
                walletSignature = signature
            ).onSuccess { uploadResult ->
                videoRepository.updateVideo(
                    record.copy(
                        irysTransactionId = uploadResult.transactionId,
                        ownerPublicKey = publicKey,
                        isEncrypted = true,
                        isUploaded = true,
                        status = VideoStatus.UPLOADED
                    )
                )
                localFile.delete()
                File(encryptResult.encryptedFilePath).delete()

                if (isNetworkShare) {
                    creditRepository.addCredits(CreditRepository.CREDITS_PER_CONTRIBUTION)
                    rewardService.addReward(RewardService.SKR_PER_CONTRIBUTION)
                    _uiState.value = _uiState.value.copy(
                        activeOperationId = null,
                        successMessage = "Contribuído! +${CreditRepository.CREDITS_PER_CONTRIBUTION} créditos · +${RewardService.SKR_PER_CONTRIBUTION} SKR pendente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        activeOperationId = null,
                        successMessage = "Salvo no Irys: ${uploadResult.transactionId.take(12)}..."
                    )
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    activeOperationId = null,
                    error = "Upload falhou: ${it.message}"
                )
            }
        }.onFailure {
            videoRepository.updateVideo(record.copy(status = VideoStatus.LOCAL))
            _uiState.value = _uiState.value.copy(
                activeOperationId = null,
                error = "Criptografia falhou: ${it.message}"
            )
        }
    }

    fun claimSkrRewards(activity: ComponentActivity) {
        viewModelScope.launch {
            rewardService.claimRewards(activity)
                .onSuccess { txId ->
                    _uiState.value = _uiState.value.copy(
                        successMessage = "SKR resgatado! TX: ${txId.take(12)}..."
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(error = it.message)
                }
        }
    }

    fun deleteVideo(record: VideoRecord) {
        if (record.isLocked) {
            _uiState.value = _uiState.value.copy(error = "Vídeo bloqueado — possível evidência de incidente")
            return
        }
        viewModelScope.launch { videoRepository.deleteVideo(record.id) }
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
                irysService = app.irysService,
                walletService = app.walletService,
                creditRepository = CreditRepository(context),
                rewardService = app.rewardService
            ) as T
        }
    }
}

data class GalleryUiState(
    val activeOperationId: String? = null,
    val successMessage: String? = null,
    val error: String? = null
)
