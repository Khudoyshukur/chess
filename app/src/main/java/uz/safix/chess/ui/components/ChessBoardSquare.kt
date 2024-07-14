package uz.safix.chess.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
    isSelectedForMove: Boolean,
    isKingAttacked: Boolean,
    engineMoveFrom: Boolean,
    engineMoveTo: Boolean,
    onClick: (index: Int) -> Unit = {},
) {
    val remainder = if ((state.index / 8) % 2 == 0) 1 else 0
    val isWhite = state.index % 2 == remainder
    val squareColor = if (isKingAttacked) {
        R.color.king_attacked
    } else if (engineMoveFrom) {
        R.color.engine_move_from
    } else if (engineMoveTo) {
        R.color.engine_move_to
    } else if (isSelectedForMove) {
        R.color.selected_piece_background
    } else if (isWhite) {
        R.color.board_square_white
    } else {
        R.color.board_square_black
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
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
                    .padding(4.dp),
            )
        }
    }
}

@Preview
@Composable
fun ChessBoardSquarePreview() {
    ChessBoardSquare(
        state = BoardSquareState(
            piece = ChessPiece.BlackKing,
            index = 0
        ),
        isSelectedForMove = false,
        engineMoveFrom = true,
        engineMoveTo = false,
        isKingAttacked = true
    )
}

