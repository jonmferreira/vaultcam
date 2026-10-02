package com.vaultcam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vaultcam.ui.screen.GalleryScreen
import com.vaultcam.ui.screen.RecordingScreen
import com.vaultcam.ui.screen.RecoveryScreen

@Composable
fun VaultCamNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Recording.route
    ) {
        composable(Screen.Recording.route) {
            RecordingScreen(
                onOpenGallery = { navController.navigate(Screen.Gallery.route) }
            )
        }

        composable(Screen.Gallery.route) {
            GalleryScreen(
                onBack = { navController.popBackStack() },
                onRecover = { videoId ->
                    navController.navigate(Screen.Recovery.createRoute(videoId))
                }
            )
        }

        composable(
            route = Screen.Recovery.route,
            arguments = listOf(navArgument("videoId") { type = NavType.StringType })
        ) { backStackEntry ->
            RecoveryScreen(
                videoId = backStackEntry.arguments?.getString("videoId") ?: "",
                onBack = { navController.popBackStack() }
            )
        }
    }
}
