package uz.safix.chess.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.bhlangonijr.chesslib.Side
import uz.kjuraev.engine.DifficultyLevel
import uz.safix.chess.ui.screens.GameScreen
import uz.safix.chess.ui.screens.HomeScreen

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:20 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

const val BUNDLE_DIFFICULTY_LEVEL = "BUNDLE_DIFFICULTY_LEVEL"


enum class ChessAppScreen(val route: String) {
    HomeScreen("HomeScreen"),
    PlayWithComputerScreen("PlayWithComputerScreen/{level}/{side}")
}

fun getPlayWithComputerScreenRoute(level: DifficultyLevel, side: Side) = "PlayWithComputerScreen/${level.name}/${side.name}"

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
                onPlayWithComputer = { level, side ->
                    navController.navigate(getPlayWithComputerScreenRoute(level, side),)
                },
                onRateApp = onRateApp,
                onContactAuthor = onContactAuthor,
                onExit = onExitApp
            )
        }

        composable(ChessAppScreen.PlayWithComputerScreen.route) {
            GameScreen(
                onFinished = { navController.popBackStack() }
            )
        }
    }
}