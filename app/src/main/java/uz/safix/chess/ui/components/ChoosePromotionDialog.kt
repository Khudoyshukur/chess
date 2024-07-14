package uz.safix.chess.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import uz.safix.chess.R

/**
 * Created by: androdev
 * Date: 14-07-2024
 * Time: 5:24 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */
@StringRes
private fun PieceType.getStringForOption() = when(this) {
    PieceType.NONE, PieceType.PAWN, PieceType.KING -> throw IllegalArgumentException("Illegal")
    PieceType.KNIGHT -> R.string.knight
    PieceType.BISHOP -> R.string.bishop
    PieceType.ROOK -> R.string.rook
    PieceType.QUEEN -> R.string.queen
}

@Composable
fun ChoosePromotionDialog(
    onOptionSelected: (PieceType) -> Unit
) {
    val options =
        remember { listOf(PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK) }
    var selectedOption by remember { mutableStateOf(PieceType.QUEEN) }

    AlertDialog(
        onDismissRequest = { },
        text = {
            Column {
                options.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option }
                        )
                        Text(
                            text = stringResource(id = option.getStringForOption()),
                            modifier = Modifier.clickable { selectedOption = option }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOptionSelected(selectedOption) }
            ) { Text(stringResource(R.string.choose)) }
        }
    )
}