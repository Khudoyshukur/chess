package uz.safix.chess.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uz.safix.chess.R

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:25 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun HomeScreen(
    onPlayWithComputer: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = onPlayWithComputer,
            modifier = Modifier.wrapContentWidth()
                .wrapContentHeight()
        ) {
            Text(text = stringResource(id = R.string.play_with_computer))
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(onPlayWithComputer = {})
}