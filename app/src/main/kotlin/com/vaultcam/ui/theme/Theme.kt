package com.vaultcam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SolanaGreen,
    onPrimary = SurfaceDark,
    secondary = SolanaPurple,
    onSecondary = OnSurface,
    tertiary = RecordRed,
    background = SurfaceDark,
    onBackground = OnSurface,
    surface = SurfaceCard,
    onSurface = OnSurface,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = OnSurfaceMuted,
    error = ErrorRed,
)

@Composable
fun VaultCamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = VaultCamTypography,
        content = content
    )
}
