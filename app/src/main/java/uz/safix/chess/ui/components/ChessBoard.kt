package uz.safix.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import uz.safix.chess.R
import uz.safix.chess.model.ChessPiece
import uz.safix.chess.model.defaultBoardState

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 9:29 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun ChessBoard(
    state: List<ChessPiece?>,
    userPlayingWithWhite: Boolean,
    modifier: Modifier = Modifier
) {
    val numbers = remember {
        val list = listOf("8", "7", "6", "5", "4", "3", "2", "1")
        if (userPlayingWithWhite) list else list.reversed()
    }
    val characters = remember { listOf("a", "b", "c", "d", "e", "f", "g", "h") }

    Row(
        modifier = modifier
            .aspectRatio(1f)
            .background(colorResource(R.color.board_background))
    ) {
        Column(
            modifier = Modifier
                .width(Dp(18f))
                .fillMaxHeight()
                .padding(bottom = Dp(18f), top = Dp(18f)),
        ) {
            numbers.forEach {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center // Center both horizontally & vertically
                ) {
                    Text(it, fontSize = 12.sp)
                }
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier
                .height(Dp(18f))
                .fillMaxWidth())
            LazyVerticalGrid(
                modifier = Modifier.weight(1f),
                columns = GridCells.Fixed(8),
                reverseLayout = userPlayingWithWhite
            ) {
                items(64) {
                    ChessBoardSquare(index = it, piece = state[it])
                }
            }
            Row(modifier = Modifier
                .height(Dp(18f))
                .fillMaxWidth()) {
                characters.forEach {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        contentAlignment = Alignment.Center // Center both horizontally & vertically
                    ) {
                        Text(it, fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier
            .width(Dp(18f))
            .fillMaxHeight())
    }
}

@Preview
@Composable
fun ChessBoardPreviewWhite() {
    ChessBoard(state = defaultBoardState, userPlayingWithWhite = true)
}

@Preview
@Composable
fun ChessBoardPreviewBlack() {
    ChessBoard(state = defaultBoardState, userPlayingWithWhite = false)
}