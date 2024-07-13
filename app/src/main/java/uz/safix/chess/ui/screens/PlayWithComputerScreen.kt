package uz.safix.chess.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uz.safix.chess.model.defaultBoardState
import uz.safix.chess.ui.components.ChessBoard

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:25 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun PlayWithComputerScreen() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        ChessBoard(state = defaultBoardState, userPlayingWithWhite = true)
    }
}

@Preview
@Composable
fun PlayWithComputerPreview() {
    PlayWithComputerScreen()
}