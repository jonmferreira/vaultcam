package com.vaultcam.service

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletService(private val context: Context) {

    companion object {
        private const val APP_IDENTITY_URI = "https://vaultcam.app"
        private const val APP_ICON_URI = "https://vaultcam.app/icon.png"
        private const val APP_NAME = "VaultCam"
    }

    private val _walletState = MutableStateFlow<WalletState>(WalletState.Disconnected)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val walletAdapter = MobileWalletAdapter(
        connectionIdentity = ConnectionIdentity(
            identityUri = Uri.parse(APP_IDENTITY_URI),
            iconUri = Uri.parse(APP_ICON_URI),
            identityName = APP_NAME
        )
    )

    suspend fun connect(activity: ComponentActivity): Result<String> = runCatching {
        val sender = ActivityResultSender(activity)
        val result = walletAdapter.connect(sender)

        when (result) {
            is TransactionResult.Success -> {
                val auth = result.authResult
                val publicKey = android.util.Base64.encodeToString(
                    auth.accounts.first().publicKey,
                    android.util.Base64.NO_WRAP
                )
                _walletState.value = WalletState.Connected(
                    publicKey = publicKey,
                    authToken = auth.authToken
                )
                publicKey
            }
            is TransactionResult.NoWalletFound ->
                throw Exception("Nenhuma wallet Solana instalada. Instale Phantom ou Solflare.")
            is TransactionResult.Failure ->
                throw Exception("Conexão falhou: ${result.e?.message}")
        }
    }

    suspend fun signMessage(
        activity: ComponentActivity,
        message: String
    ): Result<String> = runCatching {
        val state = _walletState.value as? WalletState.Connected
            ?: throw Exception("Wallet não conectada")

        val sender = ActivityResultSender(activity)
        val messageBytes = message.toByteArray(Charsets.UTF_8)

        val result = walletAdapter.transact(sender) { authResult ->
            signMessagesDetached(
                messages = arrayOf(messageBytes),
                addresses = arrayOf(authResult.accounts.first().publicKey)
            )
        }

        when (result) {
            is TransactionResult.Success -> {
                val sigBytes = result.payload.messages.first().signatures.first()
                    ?: throw Exception("Assinatura vazia")
                android.util.Base64.encodeToString(sigBytes, android.util.Base64.NO_WRAP)
            }
            is TransactionResult.NoWalletFound ->
                throw Exception("Wallet não encontrada")
            is TransactionResult.Failure ->
                throw Exception("Assinatura falhou: ${result.e?.message}")
        }
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
