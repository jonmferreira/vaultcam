package com.vaultcam.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vaultcam.VaultCamApp
import com.vaultcam.service.FleetStreamingService
import com.vaultcam.service.StreamState
import com.vaultcam.service.WalletService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FleetViewModel(
    private val fleetStreamingService: FleetStreamingService,
    private val walletService: WalletService
) : ViewModel() {

    private val _uiState = MutableStateFlow(FleetUiState())
    val uiState: StateFlow<FleetUiState> = _uiState.asStateFlow()

    val streamState = fleetStreamingService.streamState

    fun startStream(activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val publicKey = walletService.getConnectedAddress()
                ?: walletService.connect(activity).getOrElse {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                    return@launch
                }

            // Produção: gerar JWT LiveKit on-chain com verificação de licença NFT
            val token = buildLiveKitToken(publicKey)

            fleetStreamingService.startStreaming(token)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, walletAddress = publicKey)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun stopStream() {
        fleetStreamingService.stopStreaming()
    }

    override fun onCleared() {
        super.onCleared()
        fleetStreamingService.stopStreaming()
    }

    private fun buildLiveKitToken(walletAddress: String): String {
        // Stub: produção usa Solana smart contract para verificar NFT de licença
        // e servidor LiveKit para gerar JWT com claims corretos
        return "stub_token_$walletAddress"
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = context.applicationContext as VaultCamApp
            return FleetViewModel(
                fleetStreamingService = app.fleetStreamingService,
                walletService = app.walletService
            ) as T
        }
    }
}

data class FleetUiState(
    val isLoading: Boolean = false,
    val walletAddress: String? = null,
    val error: String? = null
)
