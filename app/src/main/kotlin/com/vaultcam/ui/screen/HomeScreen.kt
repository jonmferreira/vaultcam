package com.vaultcam.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import com.vaultcam.ui.theme.LockedAmber
import com.vaultcam.ui.theme.RecordRed
import com.vaultcam.ui.theme.SolanaGreen
import com.vaultcam.ui.theme.UploadedBlue
import com.vaultcam.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartMonitoring: () -> Unit,
    onOpenGallery: () -> Unit,
    onRecover: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(context))

    val videos by vm.videos.collectAsState(initial = emptyList())
    val pendingSkr by vm.pendingSkr.collectAsState(initial = 0.0)
    val pendingNetworkRequests by vm.pendingNetworkRequests.collectAsState()
    val uiState by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            vm.dismissMessage()
        }
    }

    HomeScreenContent(
        videos = videos,
        pendingSkr = pendingSkr,
        pendingNetworkRequests = pendingNetworkRequests,
        isClaimingSkr = uiState.isClaimingSkr,
        snackbarHostState = snackbarHostState,
        onClaimSkr = { vm.claimSkrRewards(activity) },
        onDismissNetwork = { vm.dismissNetworkRequests() },
        onStartMonitoring = onStartMonitoring,
        onOpenGallery = onOpenGallery,
        onRecover = onRecover
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    videos: List<VideoRecord>,
    pendingSkr: Double,
    pendingNetworkRequests: Int,
    isClaimingSkr: Boolean,
    snackbarHostState: SnackbarHostState,
    onClaimSkr: () -> Unit,
    onDismissNetwork: () -> Unit,
    onStartMonitoring: () -> Unit,
    onOpenGallery: () -> Unit,
    onRecover: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VaultCam", fontWeight = FontWeight.Bold) },
                actions = {
                    if (videos.isNotEmpty()) {
                        IconButton(onClick = onOpenGallery) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeria completa")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // SKR claim banner
            if (pendingSkr > 0) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SolanaGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Token, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "${"%.2f".format(pendingSkr)} SKR disponíveis",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Recompensas da rede P2P",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            Button(
                                onClick = onClaimSkr,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                enabled = !isClaimingSkr
                            ) {
                                Text("Resgatar", color = SolanaGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Network investigation alert
            if (pendingNetworkRequests > 0) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LockedAmber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Incidente próximo detectado",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Seus vídeos foram bloqueados como evidência. Compartilhar ganha 0,5 SKR.",
                                        color = Color.White.copy(alpha = 0.9f),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            TextButton(onClick = onDismissNetwork) {
                                Text("Ok", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Video list header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Seus vídeos (${videos.size}/10)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (videos.size > 5) {
                        TextButton(onClick = onOpenGallery) {
                            Text("Ver tudo →")
                        }
                    }
                }
            }

            if (videos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.VideoFile,
                                null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Nenhum vídeo ainda — inicie o monitoramento",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(videos.take(5), key = { it.id }) { record ->
                    HomeVideoItem(
                        record = record,
                        onClick = { if (record.isUploaded) onRecover(record.id) }
                    )
                }
            }

            // Spacer before FAB area
            item { Spacer(Modifier.height(80.dp)) }
        }

        // Start monitoring button — fixed at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onStartMonitoring,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RecordRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Monitorar Trajeto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HomeVideoItem(record: VideoRecord, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(homeStatusColor(record))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    homeFormatTimestamp(record.timestampMs),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    homeStatusLabel(record),
                    style = MaterialTheme.typography.labelSmall,
                    color = homeStatusColor(record)
                )
            }
            Icon(
                homeStatusIcon(record),
                contentDescription = null,
                tint = homeStatusColor(record),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun homeFormatTimestamp(ms: Long): String =
    SimpleDateFormat("dd/MM HH:mm", Locale("pt", "BR")).format(Date(ms))

private fun homeStatusLabel(record: VideoRecord) = when {
    record.isLocked && !record.isUploaded -> "Bloqueado — evidência pendente de upload"
    record.isUploaded -> "Irys · ${record.irysTransactionId?.take(6)}…"
    record.isEncrypted -> "Criptografado"
    record.status == VideoStatus.ENCRYPTING -> "Criptografando…"
    record.status == VideoStatus.UPLOADING -> "Enviando para Irys…"
    else -> "Local"
}

private fun homeStatusColor(record: VideoRecord) = when {
    record.isLocked -> LockedAmber
    record.isUploaded -> UploadedBlue
    record.status == VideoStatus.ENCRYPTING || record.status == VideoStatus.UPLOADING -> Color(0xFFFFB300)
    else -> Color(0xFF8A8A8A)
}

private fun homeStatusIcon(record: VideoRecord) = when {
    record.isLocked -> Icons.Default.Lock
    record.isUploaded -> Icons.Default.CloudDone
    else -> Icons.Default.FiberManualRecord
}
