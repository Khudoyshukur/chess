package uz.safix.chess.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.safix.chess.R
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
    piece: ChessPiece? = null,
    index: Int
) {
    val remainder = if ((index / 8) % 2 == 0) 1 else 0
    val isWhite = index % 2 == remainder
    val squareColor = if (isWhite) R.color.board_square_white else R.color.board_square_black

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(color = colorResource(squareColor))
    ) {
        piece?.let {
            Image(
                painter = painterResource(id = piece.getDrawable),
                contentDescription = piece.name,
                modifier = Modifier.fillMaxSize().padding(4.dp),
            )
        }
    }
}

@Preview
@Composable
fun ChessBoardSquarePreview() {
    ChessBoardSquare(piece = null, index = 0)
}

