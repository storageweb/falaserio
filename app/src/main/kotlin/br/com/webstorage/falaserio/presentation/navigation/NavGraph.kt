package br.com.webstorage.falaserio.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.webstorage.falaserio.presentation.ui.screens.CreditsScreen
import br.com.webstorage.falaserio.presentation.ui.screens.HistoryScreen
import br.com.webstorage.falaserio.presentation.ui.screens.HomeScreen

/**
 * Rotas de navegação do app.
 */
object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val CREDITS = "credits"
}

/**
 * NavGraph principal do FalaSério.
 */
@Composable
fun FalaSerioNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToCredits = { navController.navigate(Routes.CREDITS) }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CREDITS) {
            CreditsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
