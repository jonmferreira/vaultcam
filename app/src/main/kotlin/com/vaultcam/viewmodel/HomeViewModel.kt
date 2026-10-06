package com.vaultcam.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vaultcam.VaultCamApp
import com.vaultcam.data.repository.VideoRepository
import com.vaultcam.service.BluetoothAlertService
import com.vaultcam.service.RewardService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val videoRepository: VideoRepository,
    private val rewardService: RewardService,
    private val bluetoothAlertService: BluetoothAlertService
) : ViewModel() {

    val videos = videoRepository.videos
    val pendingSkr = rewardService.pendingSkr

    private val _pendingNetworkRequests = MutableStateFlow(0)
    val pendingNetworkRequests: StateFlow<Int> = _pendingNetworkRequests.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bluetoothAlertService.incomingAlerts.collect {
                _pendingNetworkRequests.value = _pendingNetworkRequests.value + 1
            }
        }
    }

    fun dismissNetworkRequests() {
        _pendingNetworkRequests.value = 0
    }

    fun claimSkrRewards(activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClaimingSkr = true)
            rewardService.claimRewards(activity)
                .onSuccess { _uiState.value = _uiState.value.copy(isClaimingSkr = false, message = "SKR resgatado com sucesso!") }
                .onFailure { _uiState.value = _uiState.value.copy(isClaimingSkr = false, message = it.message) }
        }
    }

    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = context.applicationContext as VaultCamApp
            return HomeViewModel(
                videoRepository = VideoRepository(context),
                rewardService = app.rewardService,
                bluetoothAlertService = app.bluetoothAlertService
            ) as T
        }
    }
}

data class HomeUiState(
    val isClaimingSkr: Boolean = false,
    val message: String? = null
)
