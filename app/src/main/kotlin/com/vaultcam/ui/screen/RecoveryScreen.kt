package com.vaultcam.ui.screen

import android.widget.VideoView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import com.vaultcam.service.PaymentCurrency
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.Uri
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.ui.theme.SolanaGreen
import com.vaultcam.viewmodel.RecoveryStep
import com.vaultcam.viewmodel.RecoveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(videoId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val vm: RecoveryViewModel = viewModel(factory = RecoveryViewModel.Factory(context))

    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(videoId) {
        vm.loadVideo(videoId)
    }

    RecoveryScreenContent(
        record = uiState.record,
        isLoading = uiState.isLoading,
        step = uiState.step,
        decryptedVideoPath = uiState.decryptedVideoPath,
        error = uiState.error,
        onBack = onBack,
        onRecoverOwner = { vm.connectAndRecover(activity) },
        onPayUsdc = { vm.payAndRecover(activity, PaymentCurrency.USDC) },
        onPaySkr = { vm.payAndRecover(activity, PaymentCurrency.SKR) },
        onClearVideo = { vm.clearDecryptedVideo() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreenContent(
    record: VideoRecord?,
    isLoading: Boolean,
    step: RecoveryStep,
    decryptedVideoPath: String?,
    error: String?,
    onBack: () -> Unit,
    onRecoverOwner: () -> Unit,
    onPayUsdc: () -> Unit,
    onPaySkr: () -> Unit,
    onClearVideo: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar vídeo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                decryptedVideoPath != null -> {
                    // Reproduzir vídeo descriptografado
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(Uri.parse(decryptedVideoPath))
                                start()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(240.dp)
                    )
                    Text(
                        "Vídeo em memória — não persiste após fechar",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onClearVideo) {
                        Text("Fechar vídeo")
                    }
                }

                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SolanaGreen)
                            Text(
                                stepLabel(step),
                                modifier = Modifier.padding(top = 16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                else -> {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    record?.let { rec ->
                        Text(
                            "Vídeo criptografado com Lit Protocol",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            "Irys TX: ${rec.irysTransactionId?.take(16)}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Hash: ${rec.sha256Hash?.take(16)}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    val hasIrysTx = record?.irysTransactionId != null

                    Button(
                        onClick = onRecoverOwner,
                        enabled = hasIrysTx
                    ) {
                        Icon(Icons.Default.LockOpen, null, Modifier.padding(end = 8.dp))
                        Text("Assinar com wallet (owner)")
                    }

                    OutlinedButton(
                        onClick = onPayUsdc,
                        enabled = hasIrysTx
                    ) {
                        Icon(Icons.Default.Payment, null, Modifier.padding(end = 8.dp))
                        Text("Pagar 0.10 USDC")
                    }

                    OutlinedButton(
                        onClick = onPaySkr,
                        enabled = hasIrysTx
                    ) {
                        Icon(Icons.Default.Token, null, Modifier.padding(end = 8.dp))
                        Text("Pagar 0.10 SKR")
                    }
                }
            }
        }
    }
}

private fun stepLabel(step: RecoveryStep) = when (step) {
    RecoveryStep.SIGNING -> "Aguardando assinatura da wallet..."
    RecoveryStep.DOWNLOADING -> "Baixando do Irys..."
    RecoveryStep.DECRYPTING -> "Descriptografando via Lit Protocol..."
    RecoveryStep.READY -> "Pronto"
    RecoveryStep.IDLE -> ""
}
