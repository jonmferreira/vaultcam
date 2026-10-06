package com.vaultcam.service

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Base64

/**
 * Publicação de alertas de incidente via Waku P2P (~500m online).
 * Usa Waku REST API — sem servidor central, mensagens efêmeras.
 */
class WakuService(private val httpClient: OkHttpClient) {

    companion object {
        private const val WAKU_NODE = "https://node-01.do-ams3.waku.sandbox.status.im"
        private const val TOPIC = "/vaultcam/1/incidents/proto"
        private const val CONTENT_TOPIC = "/vaultcam/1/incident-alert/json"
    }

    suspend fun publishIncident(alert: IncidentAlert): Result<Unit> = runCatching {
        val payload = JSONObject().apply {
            put("timestampMs", alert.timestampMs)
            put("latitude", alert.latitude)
            put("longitude", alert.longitude)
            put("gForce", alert.gForce)
        }.toString()

        val payloadB64 = Base64.getEncoder().encodeToString(payload.toByteArray(Charsets.UTF_8))

        val message = JSONObject().apply {
            put("payload", payloadB64)
            put("contentTopic", CONTENT_TOPIC)
            put("timestamp", System.currentTimeMillis() * 1_000_000L)  // nanoseconds
        }

        val body = message.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$WAKU_NODE/relay/v1/messages/${encode(TOPIC)}")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Waku publish failed: ${response.code}")
        }
    }

    suspend fun publishLocation(latitude: Double, longitude: Double): Result<Unit> = runCatching {
        val payload = JSONObject().apply {
            put("type", "sos_location")
            put("timestampMs", System.currentTimeMillis())
            put("latitude", latitude)
            put("longitude", longitude)
        }.toString()

        val payloadB64 = Base64.getEncoder().encodeToString(payload.toByteArray(Charsets.UTF_8))
        val message = JSONObject().apply {
            put("payload", payloadB64)
            put("contentTopic", "/vaultcam/1/sos-location/json")
            put("timestamp", System.currentTimeMillis() * 1_000_000L)
        }

        val body = message.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$WAKU_NODE/relay/v1/messages/${encode(TOPIC)}")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Waku SOS publish failed: ${response.code}")
        }
    }

    private fun encode(topic: String) =
        java.net.URLEncoder.encode(topic, "UTF-8")
}
