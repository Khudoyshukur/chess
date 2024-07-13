package uz.safix.chess.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.safix.chess.R
import uz.safix.chess.model.DifficultyLevel
import uz.safix.chess.ui.components.ChooseOptionDialog

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:25 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun HomeScreen(
    onPlayWithComputer: (level: DifficultyLevel) -> Unit,
    onRateApp: () -> Unit,
    onContactAuthor: () -> Unit,
    onExit: () -> Unit
) {
    var showDifficultyLevelDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.home_background)),
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = null
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Button(
                onClick = { showDifficultyLevelDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.button_color)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.play_with_computer),
                    color = colorResource(id = R.color.on_button_color)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onRateApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.button_color)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.rate_app),
                    color = colorResource(id = R.color.on_button_color)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onContactAuthor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.button_color)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.contact_author),
                    color = colorResource(id = R.color.on_button_color)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onExit,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.button_color)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.exit),
                    color = colorResource(id = R.color.on_button_color)
                )
            }
        }
    }

    if (showDifficultyLevelDialog) {
        val options = listOf(
            stringResource(R.string.easy),
            stringResource(R.string.middle),
            stringResource(R.string.hard),
            stringResource(R.string.extra_hard),
        )
        ChooseOptionDialog(
            options = options,
            title = stringResource(R.string.choose_difficulty),
            confirmTxt = stringResource(R.string.choose),
            dismissTxt = stringResource(R.string.cancel),
            onDismissed = { showDifficultyLevelDialog = false },
            onOptionSelected = {
                val level = when(options.indexOf(it)) {
                    0 -> DifficultyLevel.EASY
                    1 -> DifficultyLevel.MIDDLE
                    2 -> DifficultyLevel.HARD
                    3 -> DifficultyLevel.EXTRA_HARD
                    else -> null
                }
                level?.let(onPlayWithComputer)
            }
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onPlayWithComputer = {},
        onRateApp = {},
        onContactAuthor = {},
        onExit = {}
    )
}