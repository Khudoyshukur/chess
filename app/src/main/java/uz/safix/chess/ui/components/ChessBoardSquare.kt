package uz.safix.chess.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import uz.safix.chess.R
import uz.safix.chess.model.BoardSquareState
import uz.safix.chess.model.ChessPiece
import uz.safix.chess.model.getDrawable

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:52 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun ChessBoardSquare(
    state: BoardSquareState,
    onClick: (index: Int) -> Unit = {},
) {

    val remainder = if ((state.index / 8) % 2 == 0) 1 else 0
    val isWhite = state.index % 2 == remainder
    val squareColor = if (state.isKingAttacked || (state.isPossibleMove && state.piece != null)) {
        R.color.king_attacked
    } else if (state.movedFrom) {
        R.color.engine_move_from
    } else if (state.movedTo) {
        R.color.engine_move_to
    } else if (state.isSelectedForMove) {
        R.color.selected_piece_background
    } else if (isWhite) {
        R.color.board_square_white
    } else {
        R.color.board_square_black
    }

    val density = LocalDensity.current
    var squareSize by remember { mutableStateOf(IntSize.Zero) }
    val squareSizeDp = remember(squareSize) { with(density) { squareSize.height.toDp() } }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .onGloballyPositioned { squareSize = it.size }
            .background(colorResource(squareColor))
            .clickable(enabled = true, onClick = {
                 onClick(state.index)
            }),
    ) {
        state.piece?.let {
            Image(
                painter = painterResource(id = state.piece.getDrawable),
                contentDescription = state.piece.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }

        if (state.piece == null && state.isPossibleMove) {
            Box(
                modifier = Modifier
                    .padding(squareSizeDp.div(3))
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(colorResource(R.color.possible_move_empty))
            )
        }
    }
}

@Preview
@Composable
private fun ChessBoardSquarePreview() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = ChessPiece.BlackKing,
            index = 0
        )
    )
}

@Preview
@Composable
private fun KingAttacked() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = ChessPiece.BlackKing,
            index = 0,
            isKingAttacked = true
        )
    )
}

@Preview
@Composable
private fun MovedFrom() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = ChessPiece.BlackKing,
            index = 0,
            movedFrom = true
        )
    )
}

@Preview
@Composable
private fun MovedTo() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = ChessPiece.BlackKing,
            index = 0,
            movedTo = true
        )
    )
}

@Preview
@Composable
private fun SelectedForMove() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = ChessPiece.BlackKing,
            index = 0,
            isSelectedForMove = true
        )
    )
}

@Preview
@Composable
private fun PossibleMoveEmpty() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = null,
            index = 0,
            isPossibleMove = true
        )
    )
}

@Preview
@Composable
private fun PossibleMovePiece() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = ChessPiece.WhiteKing,
            index = 3,
            isPossibleMove = true
        )
    )
}

