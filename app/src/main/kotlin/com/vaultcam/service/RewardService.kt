package com.vaultcam.service

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.rewardStore by preferencesDataStore("vaultcam_rewards")

/**
 * SKR rewards para contribuições à rede de evidências (Opção 3).
 * Usuário contribui vídeo → acumula SKR pendente → resgata em batch via MWA.
 *
 * Produção: treasury multisig paga os rewards; aqui é claimable via treasury stub.
 */
class RewardService(
    private val context: Context,
    private val walletService: WalletService,
    private val solanaTokenService: SolanaTokenService,
    private val walletAdapter: MobileWalletAdapter
) {
    companion object {
        private val KEY_PENDING_SKR = doublePreferencesKey("pending_skr")
        const val SKR_PER_CONTRIBUTION = 0.50  // 0.5 SKR por vídeo contribuído

        // Treasury que distribui os rewards (produção: multisig ou programa Solana)
        const val TREASURY_WALLET = "VCam1111111111111111111111111111111111111111"
    }

    val pendingSkr: Flow<Double> = context.rewardStore.data.map {
        it[KEY_PENDING_SKR] ?: 0.0
    }

    suspend fun addReward(skrAmount: Double = SKR_PER_CONTRIBUTION) {
        context.rewardStore.edit { prefs ->
            prefs[KEY_PENDING_SKR] = (prefs[KEY_PENDING_SKR] ?: 0.0) + skrAmount
        }
    }

    /**
     * Resgata SKR acumulado para a carteira do usuário.
     * Constrói SPL Token Transfer: treasury → usuário e assina via MWA.
     */
    suspend fun claimRewards(activity: ComponentActivity): Result<String> = runCatching {
        val pending = context.rewardStore.data.map { it[KEY_PENDING_SKR] ?: 0.0 }.first()

        if (pending <= 0.0) throw Exception("Sem SKR para resgatar")

        val publicKey = walletService.getConnectedAddress()
            ?: walletService.connect(activity).getOrThrow()

        val amount   = solanaTokenService.toBaseUnits(pending, SolanaTokenService.SKR_DECIMALS)
        val mintBytes = solanaTokenService.decodeBase58(SolanaTokenService.SKR_MINT)
        val tokenProg = solanaTokenService.decodeBase58("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")

        val fromAta = deriveAta(solanaTokenService.decodeBase58(TREASURY_WALLET), mintBytes)
        val toAta   = deriveAta(solanaTokenService.decodeBase58(publicKey), mintBytes)

        val txBytes = solanaTokenService.buildTransferTransaction(
            fromAta = fromAta,
            toAta   = toAta,
            owner   = solanaTokenService.decodeBase58(TREASURY_WALLET),
            tokenProgramId = tokenProg,
            amount  = amount
        ).getOrThrow()

        val sender = ActivityResultSender(activity)
        val result = walletAdapter.transact(sender) {
            signAndSendTransactions(arrayOf(txBytes))
        }

        when (result) {
            is TransactionResult.Success -> {
                val sig = result.payload.signatures.firstOrNull()
                    ?: throw Exception("Transação sem assinatura")
                // Zera o saldo pendente após claim com sucesso
                context.rewardStore.edit { prefs -> prefs[KEY_PENDING_SKR] = 0.0 }
                solanaTokenService.encodeBase58(sig)
            }
            is TransactionResult.NoWalletFound -> throw Exception("Wallet não encontrada")
            is TransactionResult.Failure -> throw Exception("Claim falhou: ${result.e?.message}")
        }
    }

    private fun deriveAta(wallet: ByteArray, mint: ByteArray): ByteArray {
        val seeds = wallet + solanaTokenService.decodeBase58(
            "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
        ) + mint
        return java.security.MessageDigest.getInstance("SHA-256").digest(seeds).take(32).toByteArray()
    }
}
