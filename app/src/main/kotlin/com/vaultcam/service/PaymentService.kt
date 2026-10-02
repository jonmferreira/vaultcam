package com.vaultcam.service

import android.content.Context
import androidx.activity.ComponentActivity
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult

enum class PaymentCurrency { USDC, SKR }

/**
 * Pay-per-video via SPL Token transfer (USDC ou SKR).
 * Constrói a transação Solana e delega assinatura + envio ao MWA.
 */
class PaymentService(
    private val context: Context,
    private val walletService: WalletService,
    private val solanaTokenService: SolanaTokenService,
    private val walletAdapter: MobileWalletAdapter
) {
    companion object {
        const val PRICE_USDC = 0.10
        const val PRICE_SKR  = 0.10   // 0.10 SKR por vídeo (20% desconto no futuro com oracle)

        // Endereço da VaultCam treasury (recebe os pagamentos)
        // Produção: endereço real de treasury multisig
        const val TREASURY_WALLET = "VCam1111111111111111111111111111111111111111"
    }

    suspend fun payForVideo(
        activity: ComponentActivity,
        videoId: String,
        currency: PaymentCurrency = PaymentCurrency.USDC
    ): Result<String> = runCatching {
        val publicKey = walletService.getConnectedAddress()
            ?: walletService.connect(activity).getOrThrow()

        val (mint, decimals, amount, label) = when (currency) {
            PaymentCurrency.USDC -> PaymentParams(
                SolanaTokenService.USDC_MINT,
                SolanaTokenService.USDC_DECIMALS,
                solanaTokenService.toBaseUnits(PRICE_USDC, SolanaTokenService.USDC_DECIMALS),
                "USDC"
            )
            PaymentCurrency.SKR -> PaymentParams(
                SolanaTokenService.SKR_MINT,
                SolanaTokenService.SKR_DECIMALS,
                solanaTokenService.toBaseUnits(PRICE_SKR, SolanaTokenService.SKR_DECIMALS),
                "SKR"
            )
        }

        val ownerBytes  = solanaTokenService.decodeBase58(publicKey)
        val mintBytes   = solanaTokenService.decodeBase58(mint)
        val tokenProg   = solanaTokenService.decodeBase58("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")

        // Deriva ATAs (Associated Token Accounts) para source e destination
        val fromAta = deriveAta(ownerBytes, mintBytes)
        val toAta   = deriveAta(solanaTokenService.decodeBase58(TREASURY_WALLET), mintBytes)

        val txBytes = solanaTokenService.buildTransferTransaction(
            fromAta    = fromAta,
            toAta      = toAta,
            owner      = ownerBytes,
            tokenProgramId = tokenProg,
            amount     = amount
        ).getOrThrow()

        // Assina e envia via MWA
        val sender = ActivityResultSender(activity)
        val result = walletAdapter.transact(sender) {
            signAndSendTransactions(arrayOf(txBytes))
        }

        when (result) {
            is TransactionResult.Success -> {
                val sig = result.payload.signatures.firstOrNull()
                    ?: throw Exception("Transação sem assinatura")
                solanaTokenService.encodeBase58(sig)
            }
            is TransactionResult.NoWalletFound -> throw Exception("Wallet não encontrada")
            is TransactionResult.Failure -> throw Exception("Pagamento falhou: ${result.e?.message}")
        }
    }

    // ATA = PDA derivada de [wallet, TOKEN_PROGRAM, mint] sob ASSOCIATED_TOKEN_PROGRAM
    // Implementação simplificada — produção usa findProgramAddress completo
    private fun deriveAta(wallet: ByteArray, mint: ByteArray): ByteArray {
        val seeds = wallet + solanaTokenService.decodeBase58(
            "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
        ) + mint
        return java.security.MessageDigest.getInstance("SHA-256").digest(seeds).take(32).toByteArray()
    }

    private data class PaymentParams(
        val mint: String,
        val decimals: Int,
        val amount: Long,
        val label: String
    )
}
