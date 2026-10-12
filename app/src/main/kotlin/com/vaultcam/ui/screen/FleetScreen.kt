package com.vaultcam.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vaultcam.service.StreamState
import com.vaultcam.ui.theme.SolanaGreen
import com.vaultcam.viewmodel.FleetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val vm: FleetViewModel = viewModel(factory = FleetViewModel.Factory(context))

    val uiState by vm.uiState.collectAsState()
    val streamState by vm.streamState.collectAsState()

    FleetScreenContent(
        streamState = streamState,
        isLoading = uiState.isLoading,
        walletAddress = uiState.walletAddress,
        error = uiState.error,
        onBack = onBack,
        onStartStream = { vm.startStream(activity) },
        onStopStream = { vm.stopStream() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetScreenContent(
    streamState: StreamState,
    isLoading: Boolean,
    walletAddress: String?,
    error: String?,
    onBack: () -> Unit,
    onStartStream: () -> Unit,
    onStopStream: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modo Frota") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                when (streamState) {
                    is StreamState.Idle -> {
                        Icon(
                            Icons.Default.VideocamOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Stream inativo",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            "Conecte sua wallet com licença empresarial\npara iniciar o stream ao vivo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (error != null) {
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                        if (isLoading) {
                            CircularProgressIndicator(color = SolanaGreen)
                        } else {
                            Button(
                                onClick = onStartStream,
                                colors = ButtonDefaults.buttonColors(containerColor = SolanaGreen)
                            ) {
                                Icon(Icons.Default.Videocam, null, Modifier.padding(end = 8.dp))
                                Text("Iniciar stream")
                            }
                        }
                    }

                    is StreamState.Connecting -> {
                        CircularProgressIndicator(color = SolanaGreen)
                        Text("Conectando ao LiveKit...", style = MaterialTheme.typography.bodyMedium)
                    }

                    is StreamState.Streaming -> {
                        val state = streamState as StreamState.Streaming
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = SolanaGreen
                        )
                        Text("Stream ativo", style = MaterialTheme.typography.headlineMedium, color = SolanaGreen)
                        Text(
                            "Wallet: ${walletAddress?.take(16)}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${state.participantCount} viewer(s)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = onStopStream,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.VideocamOff, null, Modifier.padding(end = 8.dp))
                            Text("Parar stream")
                        }
                    }

                    is StreamState.Error -> {
                        Text(
                            "Erro: ${(streamState as StreamState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
