package com.vaultcam.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Recording : Screen("recording")
    data object Gallery : Screen("gallery")
    data object Recovery : Screen("recovery/{videoId}") {
        fun createRoute(videoId: String) = "recovery/$videoId"
    }
    data object Fleet : Screen("fleet")
}
