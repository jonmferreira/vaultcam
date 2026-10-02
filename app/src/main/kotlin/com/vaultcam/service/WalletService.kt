package com.vaultcam.service

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stub do WalletService — compila sem depender do MWA SDK.
 * TODO: substituir por implementação real com Mobile Wallet Adapter 2.x
 *       quando confirmarmos o artifact correto do Maven Central.
 */
class WalletService(private val context: Context) {

    private val _walletState = MutableStateFlow<WalletState>(WalletState.Disconnected)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    suspend fun connect(useDevnet: Boolean = true): Result<String> = runCatching {
        // TODO: integrar Mobile Wallet Adapter 2.x
        val mockAddress = "VaultCamDemoWallet1111111111111111111111111"
        _walletState.value = WalletState.Connected(
            publicKey = mockAddress,
            authToken = "mock-token"
        )
        mockAddress
    }

    suspend fun signMessage(message: String): Result<String> = runCatching {
        // TODO: assinar via MWA — retorna assinatura base64
        "mock-signature-${message.hashCode()}"
    }

    fun buildLitAuthSig(address: String, signature: String, message: String): String =
        """{"sig":"$signature","derivedVia":"solana.signMessage","signedMessage":"$message","address":"$address"}"""

    fun disconnect() {
        _walletState.value = WalletState.Disconnected
    }

    fun getConnectedAddress(): String? =
        (_walletState.value as? WalletState.Connected)?.publicKey
}

sealed class WalletState {
    data object Disconnected : WalletState()
    data class Connected(val publicKey: String, val authToken: String) : WalletState()
}
