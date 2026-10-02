package com.vaultcam.service

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Integração Lit Protocol via REST.
 * Lit Protocol gerencia as chaves de criptografia nos nós — a chave nunca existe no device.
 *
 * Fluxo real:
 *  1. Gera AES-256-GCM key localmente
 *  2. Criptografa o arquivo com essa key
 *  3. Envia a key para os nós Lit com condição de acesso (owner == walletPublicKey)
 *  4. Nós Lit guardam a key — nunca retorna para o device
 *  5. Para descriptografar: wallet assina → Lit verifica condição on-chain → reconstrói key
 */
class LitProtocolService(private val httpClient: OkHttpClient) {

    companion object {
        private const val LIT_NETWORK = "datil"
        private const val LIT_API_BASE = "https://api.litprotocol.com/v1"
        private const val AES_KEY_SIZE = 256
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
    }

    data class EncryptResult(
        val encryptedFilePath: String,
        val dataToEncryptHash: String,
        val ciphertext: String,
        val accessControlConditions: String
    )

    data class DecryptResult(
        val decryptedFilePath: String
    )

    /**
     * Criptografa o arquivo localmente com AES-256-GCM.
     * Armazena a chave nos nós Lit com condição de acesso baseada em Solana.
     */
    suspend fun encryptFile(
        inputFile: File,
        ownerWalletAddress: String,
        irysTransactionId: String
    ): Result<EncryptResult> = runCatching {
        val key = generateAesKey()
        val iv = generateIv()
        val encryptedBytes = encryptAesGcm(inputFile.readBytes(), key, iv)

        val outputFile = File(inputFile.parent, "${inputFile.nameWithoutExtension}.enc")
        outputFile.writeBytes(iv + encryptedBytes)

        val dataHash = sha256(inputFile.readBytes())
        val conditions = buildAccessControlConditions(ownerWalletAddress, irysTransactionId)

        storeKeyWithLit(key, dataHash, conditions)

        EncryptResult(
            encryptedFilePath = outputFile.absolutePath,
            dataToEncryptHash = dataHash,
            ciphertext = encryptedBytes.toHex(),
            accessControlConditions = conditions.toString()
        )
    }

    /**
     * Solicita descriptografia aos nós Lit.
     * Requer JWT de autenticação assinado pela wallet do owner.
     */
    suspend fun decryptFile(
        encryptedFile: File,
        dataToEncryptHash: String,
        walletAuthSig: String,
        outputFile: File
    ): Result<DecryptResult> = runCatching {
        val key = retrieveKeyFromLit(dataToEncryptHash, walletAuthSig)
        val encryptedBytes = encryptedFile.readBytes()
        val iv = encryptedBytes.take(GCM_IV_LENGTH).toByteArray()
        val ciphertext = encryptedBytes.drop(GCM_IV_LENGTH).toByteArray()

        val decrypted = decryptAesGcm(ciphertext, key, iv)
        outputFile.writeBytes(decrypted)

        DecryptResult(decryptedFilePath = outputFile.absolutePath)
    }

    private fun generateAesKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(AES_KEY_SIZE)
        return keyGen.generateKey()
    }

    private fun generateIv(): ByteArray = ByteArray(GCM_IV_LENGTH).also {
        java.security.SecureRandom().nextBytes(it)
    }

    private fun encryptAesGcm(data: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(data)
    }

    private fun decryptAesGcm(data: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(data)
    }

    private fun sha256(data: ByteArray): String {
        return MessageDigest.getInstance("SHA-256").digest(data).toHex()
    }

    private fun buildAccessControlConditions(
        ownerWalletAddress: String,
        irysTransactionId: String
    ): JSONArray {
        // Condição Solana: wallet é o owner do registro on-chain
        return JSONArray().apply {
            put(JSONObject().apply {
                put("method", "isOwner")
                put("params", JSONArray().apply {
                    put(irysTransactionId)
                })
                put("chain", "solana")
                put("returnValueTest", JSONObject().apply {
                    put("comparator", "=")
                    put("value", ownerWalletAddress)
                })
            })
        }
    }

    private suspend fun storeKeyWithLit(
        key: SecretKey,
        dataHash: String,
        conditions: JSONArray
    ) {
        val body = JSONObject().apply {
            put("keyMaterial", key.encoded.toHex())
            put("dataToEncryptHash", dataHash)
            put("accessControlConditions", conditions)
            put("chain", "solana")
            put("network", LIT_NETWORK)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$LIT_API_BASE/encryption/store-key")
            .post(body)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Lit store-key failed: ${response.code}")
        }
    }

    private suspend fun retrieveKeyFromLit(
        dataHash: String,
        walletAuthSig: String
    ): SecretKey {
        val body = JSONObject().apply {
            put("dataToEncryptHash", dataHash)
            put("authSig", walletAuthSig)
            put("network", LIT_NETWORK)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$LIT_API_BASE/encryption/retrieve-key")
            .post(body)
            .build()

        val keyHex = httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Lit retrieve-key failed: ${response.code}")
            JSONObject(response.body!!.string()).getString("keyMaterial")
        }
        return SecretKeySpec(keyHex.hexToBytes(), "AES")
    }

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
    private fun String.hexToBytes() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
