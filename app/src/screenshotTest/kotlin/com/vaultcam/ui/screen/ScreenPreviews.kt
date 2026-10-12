package com.vaultcam.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.service.StreamState
import com.vaultcam.ui.theme.VaultCamTheme
import com.vaultcam.viewmodel.RecoveryStep

private val previewVideos = listOf(
    VideoRecord(
        id = "1",
        timestampMs = 1_760_000_000_000L,
        isUploaded = true,
        isEncrypted = true,
        irysTransactionId = "7f3a9c91d2e4b8a0",
        sha256Hash = "a1b2c3d4e5f6a7b8",
        fileSizeBytes = 48_500_000L,
        status = VideoStatus.UPLOADED
    ),
    VideoRecord(
        id = "2",
        timestampMs = 1_759_900_000_000L,
        isLocked = true,
        fileSizeBytes = 32_100_000L,
        status = VideoStatus.LOCKED
    ),
    VideoRecord(
        id = "3",
        timestampMs = 1_759_800_000_000L,
        fileSizeBytes = 21_700_000L,
        status = VideoStatus.LOCAL
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Gallery", showBackground = true, widthDp = 400, heightDp = 880)
@Composable
private fun GalleryScreenPreview() {
    VaultCamTheme {
        GalleryScreenContent(
            videos = previewVideos,
            credits = 6,
            pendingSkr = 1.5,
            activeOperationId = null,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onClaimSkr = {},
            onUpload = {},
            onShareNetwork = {},
            onDelete = {},
            onRecover = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Recording — painel de controle", showBackground = true, widthDp = 400, heightDp = 360)
@Composable
private fun RecordingControlPanelPreview() {
    VaultCamTheme {
        RecordingControlPanel(
            modifier = Modifier.fillMaxWidth().height(360.dp),
            videoCount = 4,
            currentGForce = 1.2f,
            isSosActive = false,
            isSyncing = false,
            isRecording = true,
            onSync = {},
            onAlertNetwork = {},
            onToggleSos = {},
            onToggleRecording = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Fleet — inativo", showBackground = true, widthDp = 400, heightDp = 640)
@Composable
private fun FleetScreenIdlePreview() {
    VaultCamTheme {
        FleetScreenContent(
            streamState = StreamState.Idle,
            isLoading = false,
            walletAddress = null,
            error = null,
            onBack = {},
            onStartStream = {},
            onStopStream = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Fleet — ao vivo", showBackground = true, widthDp = 400, heightDp = 640)
@Composable
private fun FleetScreenStreamingPreview() {
    VaultCamTheme {
        FleetScreenContent(
            streamState = StreamState.Streaming(participantCount = 3),
            isLoading = false,
            walletAddress = "7f3a9c91d2e4b8a0c5d6",
            error = null,
            onBack = {},
            onStartStream = {},
            onStopStream = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Recovery", showBackground = true, widthDp = 400, heightDp = 640)
@Composable
private fun RecoveryScreenPreview() {
    VaultCamTheme {
        RecoveryScreenContent(
            record = previewVideos.first(),
            isLoading = false,
            step = RecoveryStep.IDLE,
            decryptedVideoPath = null,
            error = null,
            onBack = {},
            onRecoverOwner = {},
            onPayUsdc = {},
            onPaySkr = {},
            onClearVideo = {},
        )
    }
}
