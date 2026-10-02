package com.vaultcam.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vaultcam.VaultCamApp
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.repository.VideoRepository
import com.vaultcam.service.IrysService
import com.vaultcam.service.LitProtocolService
import com.vaultcam.service.WalletService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class RecoveryViewModel(
    private val context: Context,
    private val videoRepository: VideoRepository,
    private val litProtocolService: LitProtocolService,
    private val irysService: IrysService,
    private val walletService: WalletService,
    private val httpClient: OkHttpClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    fun loadVideo(videoId: String) {
        viewModelScope.launch {
            runCatching { videoRepository.getVideo(videoId) }
                .onSuccess { record ->
                    _uiState.value = _uiState.value.copy(record = record)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(error = "Vídeo não encontrado")
                }
        }
    }

    fun connectAndRecover(activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, step = RecoveryStep.SIGNING)
            walletService.connect(activity)
                .onSuccess { publicKey ->
                    val message = "VaultCam access ${_uiState.value.record?.id}"
                    walletService.signMessage(activity, message)
                        .onSuccess { signature ->
                            val authSig = walletService.buildLitAuthSig(publicKey, signature, message)
                            recoverVideo(authSig)
                        }
                        .onFailure {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false, step = RecoveryStep.IDLE,
                                error = "Assinatura falhou: ${it.message}"
                            )
                        }
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, step = RecoveryStep.IDLE,
                        error = it.message
                    )
                }
        }
    }

    /**
     * Fluxo de recuperação:
     * 1. Baixa o arquivo criptografado do Irys
     * 2. Wallet assina mensagem de autorização
     * 3. Lit Protocol verifica assinatura e reconstrói chave
     * 4. Descriptografa em memória — exibe sem persistir
     */
    fun recoverVideo(walletAuthSig: String) {
        val record = _uiState.value.record ?: return
        val txId = record.irysTransactionId ?: return
        val dataHash = record.sha256Hash ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, step = RecoveryStep.DOWNLOADING)

            val downloadUrl = irysService.getDownloadUrl(txId)
            val encryptedFile = File(context.cacheDir, "$txId.enc")

            runCatching {
                val request = Request.Builder().url(downloadUrl).get().build()
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw Exception("Download falhou: ${response.code}")
                    encryptedFile.writeBytes(response.body!!.bytes())
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Download falhou: ${it.message}"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(step = RecoveryStep.DECRYPTING)

            val outputFile = File(context.cacheDir, "$txId.mp4")
            litProtocolService.decryptFile(
                encryptedFile = encryptedFile,
                dataToEncryptHash = dataHash,
                walletAuthSig = walletAuthSig,
                outputFile = outputFile
            ).onSuccess {
                encryptedFile.delete()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    step = RecoveryStep.READY,
                    decryptedVideoPath = outputFile.absolutePath
                )
            }.onFailure {
                encryptedFile.delete()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    step = RecoveryStep.IDLE,
                    error = "Descriptografia falhou: ${it.message}"
                )
            }
        }
    }

    fun clearDecryptedVideo() {
        val path = _uiState.value.decryptedVideoPath
        path?.let { File(it).delete() }
        _uiState.value = _uiState.value.copy(decryptedVideoPath = null, step = RecoveryStep.IDLE)
    }

    override fun onCleared() {
        super.onCleared()
        clearDecryptedVideo()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = context.applicationContext as VaultCamApp
            return RecoveryViewModel(
                context = context,
                videoRepository = VideoRepository(context),
                litProtocolService = app.litProtocolService,
                irysService = app.irysService,
                walletService = app.walletService,
                httpClient = app.okHttpClient
            ) as T
        }
    }
}

enum class RecoveryStep { IDLE, SIGNING, DOWNLOADING, DECRYPTING, READY }

data class RecoveryUiState(
    val record: VideoRecord? = null,
    val isLoading: Boolean = false,
    val step: RecoveryStep = RecoveryStep.IDLE,
    val decryptedVideoPath: String? = null,
    val error: String? = null
)
