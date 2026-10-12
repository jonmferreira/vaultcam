package com.vaultcam.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.ui.theme.VaultCamTheme

private val sampleVideos = listOf(
    VideoRecord(
        id = "1",
        timestampMs = 1_760_000_000_000L,
        isUploaded = true,
        irysTransactionId = "7f3a9c91d2",
        status = VideoStatus.UPLOADED
    ),
    VideoRecord(
        id = "2",
        timestampMs = 1_759_900_000_000L,
        isLocked = true,
        status = VideoStatus.LOCKED
    ),
    VideoRecord(
        id = "3",
        timestampMs = 1_759_800_000_000L,
        isEncrypted = true,
        status = VideoStatus.ENCRYPTED
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Home — com vídeos e alertas", showBackground = true, widthDp = 400, heightDp = 880)
@Composable
private fun HomeScreenPreview() {
    VaultCamTheme {
        HomeScreenContent(
            videos = sampleVideos,
            pendingSkr = 1.5,
            pendingNetworkRequests = 1,
            isClaimingSkr = false,
            snackbarHostState = remember { SnackbarHostState() },
            onClaimSkr = {},
            onDismissNetwork = {},
            onStartMonitoring = {},
            onOpenGallery = {},
            onRecover = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Home — vazia", showBackground = true, widthDp = 400, heightDp = 880)
@Composable
private fun HomeScreenEmptyPreview() {
    VaultCamTheme {
        HomeScreenContent(
            videos = emptyList(),
            pendingSkr = 0.0,
            pendingNetworkRequests = 0,
            isClaimingSkr = false,
            snackbarHostState = remember { SnackbarHostState() },
            onClaimSkr = {},
            onDismissNetwork = {},
            onStartMonitoring = {},
            onOpenGallery = {},
            onRecover = {},
        )
    }
}
