package uz.safix.chess.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.safix.chess.R
import uz.safix.chess.model.DifficultyLevel
import uz.safix.chess.ui.components.ChessBoard
import uz.safix.chess.ui.viewmodels.GameViewModel

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:25 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.home_background)
    ) {
        ChessBoard(
            states = gameState,
            userPlayingWithWhite = true,
            onClick = viewModel::onClick
        )
    }
}

@Preview
@Composable
fun PlayWithComputerPreview() {
    GameScreen()
}