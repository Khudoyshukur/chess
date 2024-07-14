package uz.safix.chess.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.bhlangonijr.chesslib.game.GameResult
import uz.safix.chess.R

/**
 * Created by: androdev
 * Date: 14-07-2024
 * Time: 2:57 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun GameResultDialog(
    gameResult: GameResult,
    onConfirmed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        text = {
            Text(
                text = when (gameResult) {
                    GameResult.WHITE_WON -> stringResource(R.string.white_won)
                    GameResult.BLACK_WON -> stringResource(R.string.black_won)
                    GameResult.DRAW -> stringResource(R.string.draw)
                    GameResult.ONGOING -> ""
                }
            )
        },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onConfirmed
            ) { Text(stringResource(R.string.close)) }
        }
    )
}