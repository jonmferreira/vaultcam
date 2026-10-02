package com.vaultcam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoRecord(
    val id: String,
    val localPath: String? = null,
    val irysTransactionId: String? = null,
    val sha256Hash: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestampMs: Long,
    val isEncrypted: Boolean = false,
    val isLocked: Boolean = false,
    val isUploaded: Boolean = false,
    val ownerPublicKey: String? = null,
    val durationSeconds: Int = 0,
    val fileSizeBytes: Long = 0L,
    val status: VideoStatus = VideoStatus.LOCAL
)

@Serializable
enum class VideoStatus {
    RECORDING,
    LOCAL,
    ENCRYPTING,
    ENCRYPTED,
    UPLOADING,
    UPLOADED,
    LOCKED
}
