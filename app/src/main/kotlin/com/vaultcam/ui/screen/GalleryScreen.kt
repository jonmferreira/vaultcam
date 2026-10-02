package com.vaultcam.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.ui.theme.LockedAmber
import com.vaultcam.ui.theme.SolanaGreen
import com.vaultcam.ui.theme.UploadedBlue
import com.vaultcam.viewmodel.GalleryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(onBack: () -> Unit, onRecover: (String) -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val vm: GalleryViewModel = viewModel(factory = GalleryViewModel.Factory(context))

    val uiState by vm.uiState.collectAsState()
    val videos by vm.videos.collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            vm.dismissMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.dismissMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VaultCam — ${videos.size}/10 vídeos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (videos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.VideoFile,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Nenhum vídeo gravado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(videos, key = { it.id }) { record ->
                    VideoCard(
                        record = record,
                        isProcessing = uiState.activeOperationId == record.id,
                        onUpload = { vm.encryptAndUpload(record, activity) },
                        onDelete = { vm.deleteVideo(record) },
                        onPlay = { if (record.isUploaded) onRecover(record.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoCard(
    record: VideoRecord,
    isProcessing: Boolean,
    onUpload: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onPlay() },
                contentAlignment = Alignment.Center
            ) {
                if (record.isLocked) {
                    Icon(Icons.Default.Lock, null, tint = LockedAmber, modifier = Modifier.size(28.dp))
                } else {
                    Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formatTimestamp(record.timestampMs),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    statusLabel(record),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor(record)
                )
                Text(
                    formatSize(record.fileSizeBytes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Row {
                    if (!record.isUploaded && !record.isLocked) {
                        IconButton(onClick = onUpload) {
                            Icon(Icons.Default.CloudUpload, "Upload", tint = SolanaGreen)
                        }
                    }
                    if (!record.isLocked) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, "Deletar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(ms: Long): String =
    SimpleDateFormat("dd/MM HH:mm:ss", Locale("pt", "BR")).format(Date(ms))

private fun formatSize(bytes: Long): String = when {
    bytes < 1024 -> "${bytes}B"
    bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024f)}KB"
    else -> "${"%.1f".format(bytes / (1024f * 1024f))}MB"
}

private fun statusLabel(record: VideoRecord) = when {
    record.isLocked -> "BLOQUEADO — evidência"
    record.isUploaded -> "Irys · ${record.irysTransactionId?.take(8)}..."
    record.isEncrypted -> "Criptografado"
    record.status == VideoStatus.ENCRYPTING -> "Criptografando..."
    record.status == VideoStatus.UPLOADING -> "Enviando para Irys..."
    else -> "Local"
}

private fun statusColor(record: VideoRecord) = when {
    record.isLocked -> LockedAmber
    record.isUploaded -> UploadedBlue
    record.status == VideoStatus.ENCRYPTING || record.status == VideoStatus.UPLOADING -> Color(0xFFFFB300)
    else -> Color(0xFF8A8A8A)
}
