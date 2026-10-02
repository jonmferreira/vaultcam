package com.vaultcam.service

import android.content.Context
import androidx.activity.ComponentActivity
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.TransactionResult

/**
 * USDC pay-per-video stub.
 * Produção: construir transação SPL Token transfer e submeter via MWA.
 * Aqui: simula aprovação de pagamento via assinatura de mensagem.
 */
class PaymentService(
    private val context: Context,
    private val walletService: WalletService
) {
    companion object {
        const val PRICE_PER_VIDEO_USDC = 0.10
        private const val USDC_MINT = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"
    }

    suspend fun payForVideo(
        activity: ComponentActivity,
        videoId: String
    ): Result<String> = runCatching {
        val publicKey = walletService.getConnectedAddress()
            ?: walletService.connect(activity).getOrThrow()

        // Stub: assina mensagem de autorização de pagamento
        // Produção: construir instrução SPL Token transfer USDC aqui
        val authMessage = "VaultCam pay $PRICE_PER_VIDEO_USDC USDC for video $videoId"
        val signature = walletService.signMessage(activity, authMessage).getOrThrow()

        // Produção: submeter tx on-chain e retornar txId real
        "stub_payment_tx_${videoId.take(8)}_${signature.take(8)}"
    }
}
