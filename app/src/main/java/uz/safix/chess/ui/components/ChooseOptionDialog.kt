package uz.safix.chess.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 7:51 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */
@Composable
fun ChooseOptionDialog(
    options: List<String>,
    title: String,
    confirmTxt: String,
    dismissTxt: String,
    onDismissed: () -> Unit,
    onOptionSelected: (option: String) -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf(options[0]) }

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
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onOptionSelected(selectedOption)
                    onDismissed()
                }
            ) { Text(confirmTxt) }
        }
    )
}