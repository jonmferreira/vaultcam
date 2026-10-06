package com.vaultcam.ui.screen

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.vaultcam.service.RecordingState
import com.vaultcam.ui.theme.LockedAmber
import com.vaultcam.ui.theme.RecordRed
import com.vaultcam.ui.theme.SolanaGreen
import com.vaultcam.viewmodel.RecordingViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecordingScreen(
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenFleet: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val vm: RecordingViewModel = viewModel(factory = RecordingViewModel.Factory(context))

    val uiState by vm.uiState.collectAsState()
    val recordingState by vm.recordingState.collectAsState()
    val videos by vm.videos.collectAsState(initial = emptyList())

    val permissions = rememberMultiplePermissionsState(
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    )

    LaunchedEffect(Unit) { permissions.launchMultiplePermissionRequest() }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(permissions.allPermissionsGranted) {
        if (permissions.allPermissionsGranted) vm.bindCamera(lifecycleOwner, previewView)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (recordingState is RecordingState.Recording) {
                            RecIndicator()
                            Spacer(Modifier.width(8.dp))
                            Text(formatElapsed(uiState.elapsedSeconds))
                        } else {
                            Text("Monitorar Trajeto")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenFleet) {
                        Icon(Icons.Default.Business, contentDescription = "Frota")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Camera preview — 55% da tela
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
                    .background(Color.Black)
            ) {
                if (permissions.allPermissionsGranted) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Button(onClick = { permissions.launchMultiplePermissionRequest() }) {
                            Text("Permitir câmera")
                        }
                    }
                }
            }

            // Painel inferior — 45%
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(label = "${videos.size} vídeos", tint = MaterialTheme.colorScheme.primary)
                    StatusChip(
                        label = "GPS",
                        tint = SolanaGreen,
                        icon = { Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), tint = SolanaGreen) }
                    )
                    if (uiState.isSosActive) {
                        StatusChip(label = "SOS ATIVO", tint = RecordRed)
                    }
                }

                // G-force bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("G-force", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${"%.1f".format(uiState.currentGForce)}G",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.currentGForce > 1.5f) LockedAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (uiState.currentGForce / 2.5f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = when {
                            uiState.currentGForce > 2.0f -> RecordRed
                            uiState.currentGForce > 1.0f -> LockedAmber
                            else -> SolanaGreen
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // Alert combo buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { vm.manualLockAndSync() },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isSyncing
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚡", style = MaterialTheme.typography.titleSmall)
                            Text("Sincronizar", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    FilledTonalButton(
                        onClick = { vm.manualAlertNetwork() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = LockedAmber.copy(alpha = 0.15f),
                            contentColor = LockedAmber
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚠️", style = MaterialTheme.typography.titleSmall)
                            Text("Alertar rede", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // SOS button
                Button(
                    onClick = { if (uiState.isSosActive) vm.stopSOS() else vm.manualSOS() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isSosActive) RecordRed else RecordRed.copy(alpha = 0.85f)
                    )
                ) {
                    Text(
                        if (uiState.isSosActive) "🚨 SOS ATIVO — Toque para cancelar" else "🚨 SOS — Sinc + Rede + GPS ao vivo",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Record toggle
                Button(
                    onClick = { vm.toggleRecording() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (recordingState is RecordingState.Recording)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (recordingState is RecordingState.Recording) "⏹ Parar gravação" else "▶ Iniciar gravação",
                        color = if (recordingState is RecordingState.Recording)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Impact alert dialog
        if (uiState.impactDetected) {
            AlertDialog(
                onDismissRequest = { vm.dismissImpactAlert() },
                icon = { Icon(Icons.Default.Warning, null, tint = RecordRed) },
                title = { Text("Impacto detectado") },
                text = {
                    Text("G-force: ${"%.1f".format(uiState.lastImpactGForce)}G\nVídeos bloqueados como evidência.")
                },
                confirmButton = {
                    Button(onClick = { vm.dismissImpactAlert(); onOpenGallery() }) {
                        Text("Ver galeria")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { vm.dismissImpactAlert() }) { Text("Fechar") }
                }
            )
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    tint: Color,
    icon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .background(tint.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.invoke()
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RecIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(RecordRed.copy(alpha = alpha))
    )
}

private fun formatElapsed(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}
