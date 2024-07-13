package uz.safix.chess.ui

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.safix.chess.model.DifficultyLevel
import uz.safix.chess.ui.screens.HomeScreen
import uz.safix.chess.ui.screens.GameScreen

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:20 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

const val BUNDLE_DIFFICULTY_LEVEL = "BUNDLE_DIFFICULTY_LEVEL"


enum class ChessAppScreen(val route: String) {
    HomeScreen("HomeScreen"),
    PlayWithComputerScreen("PlayWithComputerScreen/{level}")
}

fun getPlayWithComputerScreenRoute(level: DifficultyLevel) = "PlayWithComputerScreen/${level.name}"

@Composable
fun ChessApp(
    navController: NavHostController = rememberNavController(),
    onExitApp: () -> Unit = {},
    onRateApp: () -> Unit = {},
    onContactAuthor: () -> Unit = {},
) {
    NavHost(navController = navController, startDestination = ChessAppScreen.HomeScreen.route) {
        composable(ChessAppScreen.HomeScreen.route) {
            HomeScreen(
                onPlayWithComputer = { level ->
                    navController.navigate(getPlayWithComputerScreenRoute(level),)
                },
                onRateApp = onRateApp,
                onContactAuthor = onContactAuthor,
                onExit = onExitApp
            )
        }

        composable(ChessAppScreen.PlayWithComputerScreen.route) {
            GameScreen()
        }
    }
}