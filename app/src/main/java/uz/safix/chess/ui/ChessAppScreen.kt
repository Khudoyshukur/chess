package uz.safix.chess.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.safix.chess.ui.screens.HomeScreen
import uz.safix.chess.ui.screens.GameScreen

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:20 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

enum class ChessAppScreen {
    HomeScreen, PlayWithComputerScreen
}

@Composable
fun ChessApp(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = ChessAppScreen.HomeScreen.name) {
        composable(ChessAppScreen.HomeScreen.name) {
            HomeScreen(
                onPlayWithComputer = {
                    navController.navigate(ChessAppScreen.PlayWithComputerScreen.name)
                }
            )
        }

        composable(ChessAppScreen.PlayWithComputerScreen.name) {
            GameScreen()
        }
    }
}