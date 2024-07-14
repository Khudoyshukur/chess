package uz.safix.chess.ui.components

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
import com.github.bhlangonijr.chesslib.Side
import uz.safix.chess.R

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 7:51 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun ChooseChessOptionsDialog(
    options: List<String>,
    title: String,
    confirmTxt: String,
    onDismissed: () -> Unit,
    onOptionSelected: (option: String, side: Side) -> Unit = { _, _ -> }
) {
    var selectedOption by remember { mutableStateOf(options[0]) }
    var selectedSide by remember { mutableStateOf(Side.WHITE) }

    AlertDialog(
        onDismissRequest = { onDismissed() },
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.clickable { selectedOption = option }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedSide == Side.WHITE),
                        onClick = { selectedSide = Side.WHITE }
                    )
                    Text(
                        text = stringResource(id = R.string.white),
                        modifier = Modifier.clickable { selectedSide = Side.WHITE }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedSide == Side.BLACK),
                        onClick = { selectedSide = Side.BLACK }
                    )
                    Text(
                        text = stringResource(id = R.string.black),
                        modifier = Modifier.clickable { selectedSide = Side.BLACK }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onOptionSelected(selectedOption, selectedSide)
                    onDismissed()
                }
            ) { Text(confirmTxt) }
        }
    )
}