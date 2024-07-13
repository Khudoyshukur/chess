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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val bgImage = painterResource(id = R.drawable.home_background)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.home_background)),
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            painter = bgImage,
            contentDescription = null
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Button(
                onClick = onPlayWithComputer,
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
                onClick = onPlayWithComputer,
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
                onClick = onPlayWithComputer,
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
                onClick = onPlayWithComputer,
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
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(onPlayWithComputer = {})
}