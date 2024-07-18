package uz.safix.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import uz.safix.chess.R
import uz.safix.chess.model.BoardSquareState
import uz.safix.chess.model.defaultBoardState
import androidx.compose.foundation.lazy.grid.items

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 9:29 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    userPlayingWithWhite: Boolean,
    states: List<BoardSquareState>,
    onClick: (index: Int) -> Unit = {}
) {
    Row(
        modifier = modifier
            .aspectRatio(1f)
            .background(colorResource(R.color.board_background))
    ) {
        NumbersColumn(userPlayingWithWhite)
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier
                .height(Dp(18f))
                .fillMaxWidth())
            if (userPlayingWithWhite) {
                WhitePlayingChessBoard(states, onClick)
            } else {
                BlackPlayingChessBoard(states, onClick)
            }
            CharactersRow(userPlayingWithWhite)
        }
        Spacer(modifier = Modifier
            .width(Dp(18f))
            .fillMaxHeight())
    }
}


@Composable
private fun Main(
    modifier: Modifier = Modifier,
    userPlayingWithWhite: Boolean,
    states: List<BoardSquareState>,
    onClick: (index: Int) -> Unit = {}
) {

}

@Composable
fun NumbersColumn(userPlayingWithWhite: Boolean) {
    val numbers by remember {
        val list = listOf("8", "7", "6", "5", "4", "3", "2", "1")
        derivedStateOf { if (userPlayingWithWhite) list else list.reversed() }
    }

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
}

@Composable
fun CharactersRow(userPlayingWithWhite: Boolean) {
    val characters by remember {
        val list = listOf("a", "b", "c", "d", "e", "f", "g", "h")
        derivedStateOf { if (userPlayingWithWhite) list else list.reversed() }
    }

    Row(
        modifier = Modifier
            .height(Dp(18f))
            .fillMaxWidth()
    ) {
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

@Composable
fun ColumnScope.WhitePlayingChessBoard(
    states: List<BoardSquareState>,
    onClick: (index: Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.weight(1f),
        columns = GridCells.Fixed(8),
        reverseLayout = true
    ) {
        items(items = states, key = { state -> state.index }) {
            ChessBoardSquare(state = it, onClick = onClick)
        }
    }
}

@Composable
fun ColumnScope.BlackPlayingChessBoard(
    states: List<BoardSquareState>,
    onClick: (index: Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.weight(1f),
        columns = GridCells.Fixed(8),
    ) {
        items(64, key = { states[it].index }) {
            val index = ((it / 8) * 16 + 7) - it
            ChessBoardSquare(
                state = states[index],
                onClick = onClick
            )
        }
    }
}

@Preview
@Composable
fun ChessBoardPreviewWhite() {
    ChessBoard(
        states = defaultBoardState,
        userPlayingWithWhite = true,
    )
}

@Preview
@Composable
fun ChessBoardPreviewBlack() {
    ChessBoard(
        states = defaultBoardState,
        userPlayingWithWhite = false
    )
}