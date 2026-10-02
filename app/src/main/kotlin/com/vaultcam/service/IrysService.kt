package com.vaultcam.service

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

/**
 * Upload de arquivos para Irys (Arweave-backed, imutável).
 * Usa REST API via OkHttp — sem SDK nativo Android.
 */
class IrysService(private val httpClient: OkHttpClient) {

    companion object {
        // Irys node — devnet para testes, mainnet para produção
        private const val IRYS_NODE_DEVNET = "https://devnet.irys.xyz"
        private const val IRYS_NODE_MAINNET = "https://node2.irys.xyz"
        private const val IRYS_GATEWAY = "https://gateway.irys.xyz"
        private const val SOLANA_TOKEN = "solana"
    }

    data class UploadResult(
        val transactionId: String,
        val url: String,
        val size: Long,
        val timestamp: Long
    )

    /**
     * Faz upload de arquivo para Irys com metadata VaultCam.
     * Requer assinatura da wallet Solana para autenticar.
     */
    suspend fun uploadEncryptedVideo(
        encryptedFile: File,
        ownerPublicKey: String,
        sha256Hash: String,
        latitude: Double?,
        longitude: Double?,
        timestampMs: Long,
        walletSignature: String,
        useDevnet: Boolean = true
    ): Result<UploadResult> = runCatching {
        val node = if (useDevnet) IRYS_NODE_DEVNET else IRYS_NODE_MAINNET
        val tags = buildTags(ownerPublicKey, sha256Hash, latitude, longitude, timestampMs)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                encryptedFile.name,
                encryptedFile.asRequestBody("application/octet-stream".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("$node/upload")
            .post(requestBody)
            .addHeader("x-pub-key", ownerPublicKey)
            .addHeader("x-signature", walletSignature)
            .addHeader("x-tags", tags)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Irys upload failed: ${response.code} ${response.body?.string()}")
            val json = JSONObject(response.body!!.string())
            val txId = json.getString("id")
            UploadResult(
                transactionId = txId,
                url = "$IRYS_GATEWAY/$txId",
                size = encryptedFile.length(),
                timestamp = timestampMs
            )
        }
    }

    /**
     * Verifica se uma transação foi confirmada no Arweave.
     */
    suspend fun verifyTransaction(transactionId: String): Result<Boolean> = runCatching {
        val request = Request.Builder()
            .url("$IRYS_GATEWAY/tx/$transactionId/status")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            response.isSuccessful
        }
    }

    /**
     * Retorna URL de download de um arquivo via Irys Gateway.
     */
    fun getDownloadUrl(transactionId: String) = "$IRYS_GATEWAY/$transactionId"

    private fun buildTags(
        ownerPublicKey: String,
        sha256Hash: String,
        latitude: Double?,
        longitude: Double?,
        timestampMs: Long
    ): String {
        val tags = mutableListOf(
            "App-Name" to "VaultCam",
            "App-Version" to "0.1.0",
            "Content-Type" to "application/octet-stream",
            "Owner-Public-Key" to ownerPublicKey,
            "SHA-256-Hash" to sha256Hash,
            "Timestamp" to timestampMs.toString(),
        )
        latitude?.let { tags.add("GPS-Lat" to it.toString()) }
        longitude?.let { tags.add("GPS-Lng" to it.toString()) }
        return tags.joinToString(",") { (k, v) -> "$k:$v" }
    }
}
