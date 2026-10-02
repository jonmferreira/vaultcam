package com.vaultcam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vaultcam.ui.navigation.VaultCamNavGraph
import com.vaultcam.ui.theme.VaultCamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VaultCamTheme {
                VaultCamNavGraph()
            }
        }
    }
}
