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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.vaultcam.service.RecordingState
import com.vaultcam.ui.theme.RecordRed
import com.vaultcam.ui.theme.SolanaGreen
import com.vaultcam.viewmodel.RecordingViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingScreen(onOpenGallery: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val vm: RecordingViewModel = viewModel(factory = RecordingViewModel.Factory(context))

    val uiState by vm.uiState.collectAsState()
    val recordingState by vm.recordingState.collectAsState()

    val permissions = rememberMultiplePermissionsState(
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    )

    LaunchedEffect(Unit) {
        permissions.launchMultiplePermissionRequest()
    }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(permissions.allPermissionsGranted) {
        if (permissions.allPermissionsGranted) {
            vm.bindCamera(lifecycleOwner, previewView)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (permissions.allPermissionsGranted) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Permissão de câmera necessária", color = Color.White)
                    Button(onClick = { permissions.launchMultiplePermissionRequest() }) {
                        Text("Conceder")
                    }
                }
            }
        }

        // REC indicator
        if (recordingState is RecordingState.Recording) {
            RecIndicator(modifier = Modifier.align(Alignment.TopStart).padding(16.dp))
        }

        // Controls bottom bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenGallery) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "Galeria",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            RecordButton(
                isRecording = recordingState is RecordingState.Recording,
                onClick = { vm.toggleRecording() }
            )

            // Placeholder for symmetry
            Box(modifier = Modifier.size(48.dp))
        }

        // Impact alert dialog
        if (uiState.impactDetected) {
            AlertDialog(
                onDismissRequest = { vm.dismissImpactAlert() },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = RecordRed) },
                title = { Text("Impacto detectado") },
                text = {
                    Text("G-force: ${"%.1f".format(uiState.lastImpactGForce)}G\nVídeos recentes bloqueados como evidência.")
                },
                confirmButton = {
                    Button(onClick = {
                        vm.dismissImpactAlert()
                        onOpenGallery()
                    }) { Text("Ver galeria") }
                },
                dismissButton = {
                    TextButton(onClick = { vm.dismissImpactAlert() }) { Text("Fechar") }
                }
            )
        }
    }
}

@Composable
private fun RecordButton(isRecording: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.1f else 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (isRecording) RecordRed else Color.White)
            .border(3.dp, if (isRecording) Color.White else Color.Gray, CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(if (isRecording) 28.dp else 36.dp)
                .clip(if (isRecording) RoundedCornerShape(4.dp) else CircleShape)
                .background(if (isRecording) Color.White else RecordRed)
        )
    }
}

@Composable
private fun RecIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alpha"
    )

    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(RecordRed.copy(alpha = alpha))
        )
        Text("REC", color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}
