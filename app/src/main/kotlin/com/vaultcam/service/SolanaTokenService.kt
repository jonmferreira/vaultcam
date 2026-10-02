package com.vaultcam.service

import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Constrói transações SPL Token Transfer para SKR e USDC.
 * Usa Solana JSON-RPC para blockhash recente.
 * Assinatura é delegada ao MWA (wallet do usuário).
 */
class SolanaTokenService(private val httpClient: OkHttpClient) {

    companion object {
        const val SKR_MINT  = "SKRbvo6Gf7GondiT3BbTfuRDPqLWei4j2Qy2NPGZhW3"
        const val USDC_MINT = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"

        private const val TOKEN_PROGRAM    = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
        private const val ASSOC_TOKEN_PROG = "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJe1bse"
        private const val SYSTEM_PROGRAM   = "11111111111111111111111111111112"
        private const val SOL_RPC          = "https://api.mainnet-beta.solana.com"
        // Devnet para testes: "https://api.devnet.solana.com"

        // SKR decimals = 6 (verify on-chain before mainnet)
        const val SKR_DECIMALS  = 6
        const val USDC_DECIMALS = 6

        // Preço de referência para demo (produção: oracle de preço)
        const val SKR_PRICE_PER_VIDEO = 0.10   // 0.10 SKR por vídeo
        const val USDC_PRICE_PER_VIDEO = 0.10  // 0.10 USDC por vídeo
    }

    /**
     * Retorna os bytes de uma transação SPL Token Transfer pronta para assinar via MWA.
     * [fromAta] → ATA do pagador para o mint
     * [toAta]   → ATA do recebedor para o mint
     * [owner]   → public key do pagador (signer)
     * [amount]  → quantidade em unidades base (ex: 100_000 = 0.1 USDC com 6 decimais)
     */
    suspend fun buildTransferTransaction(
        fromAta: ByteArray,
        toAta: ByteArray,
        owner: ByteArray,
        tokenProgramId: ByteArray,
        amount: Long
    ): Result<ByteArray> = runCatching {
        val blockhash = getRecentBlockhash()
        buildSplTransferMessage(fromAta, toAta, owner, tokenProgramId, amount, blockhash)
    }

    suspend fun getRecentBlockhash(): ByteArray {
        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", 1)
            put("method", "getLatestBlockhash")
            put("params", JSONArray().apply {
                put(JSONObject().apply { put("commitment", "finalized") })
            })
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder().url(SOL_RPC).post(body).build()
        val blockhashB58 = httpClient.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw Exception("RPC error: ${resp.code}")
            JSONObject(resp.body!!.string())
                .getJSONObject("result")
                .getJSONObject("value")
                .getString("blockhash")
        }
        return decodeBase58(blockhashB58)
    }

    /**
     * Constrói message de transação Solana com instrução SPL Transfer.
     * Formato: [header][accounts][blockhash][instructions]
     */
    private fun buildSplTransferMessage(
        fromAta: ByteArray,
        toAta: ByteArray,
        owner: ByteArray,
        tokenProgram: ByteArray,
        amount: Long,
        blockhash: ByteArray
    ): ByteArray {
        // Instruction data: tag=3 (Transfer), amount u64 LE
        val instrData = ByteBuffer.allocate(9).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            put(3)           // SPL Transfer instruction
            putLong(amount)
        }.array()

        val accounts = listOf(owner, fromAta, toAta, tokenProgram)

        val buf = mutableListOf<Byte>()

        // Header: num_required_signatures=1, num_readonly_signed=0, num_readonly_unsigned=1
        buf.add(1); buf.add(0); buf.add(1)

        // Account count + accounts
        buf.add(accounts.size.toByte())
        accounts.forEach { buf.addAll(it.toList()) }

        // Recent blockhash
        buf.addAll(blockhash.toList())

        // Instructions count
        buf.add(1)
        // Instruction: program_id_index=3 (tokenProgram), accounts=[1(fromAta),2(toAta),0(owner)], data
        buf.add(3)  // program index
        buf.add(3)  // account count
        buf.add(1); buf.add(2); buf.add(0)  // fromAta, toAta, owner
        buf.addAll(encodeCompactU16(instrData.size))
        buf.addAll(instrData.toList())

        return buf.toByteArray()
    }

    fun toBaseUnits(amount: Double, decimals: Int): Long =
        (amount * Math.pow(10.0, decimals.toDouble())).toLong()

    // Base58 decode (apenas caracteres Solana)
    fun decodeBase58(input: String): ByteArray {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        var num = java.math.BigInteger.ZERO
        for (c in input) {
            num = num.multiply(java.math.BigInteger.valueOf(58))
                .add(java.math.BigInteger.valueOf(alphabet.indexOf(c).toLong()))
        }
        val bytes = num.toByteArray()
        val leadingZeros = input.takeWhile { it == '1' }.length
        return ByteArray(leadingZeros) + bytes.dropWhile { it == 0.toByte() }.toByteArray()
    }

    fun encodeBase58(bytes: ByteArray): String {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        var num = java.math.BigInteger(1, bytes)
        val sb = StringBuilder()
        val fifty8 = java.math.BigInteger.valueOf(58)
        while (num > java.math.BigInteger.ZERO) {
            val (div, rem) = num.divideAndRemainder(fifty8)
            sb.insert(0, alphabet[rem.toInt()])
            num = div
        }
        bytes.takeWhile { it == 0.toByte() }.forEach { sb.insert(0, '1') }
        return sb.toString()
    }

    private fun encodeCompactU16(value: Int): List<Byte> {
        val result = mutableListOf<Byte>()
        var v = value
        do {
            var b = v and 0x7F
            v = v shr 7
            if (v != 0) b = b or 0x80
            result.add(b.toByte())
        } while (v != 0)
        return result
    }
}
