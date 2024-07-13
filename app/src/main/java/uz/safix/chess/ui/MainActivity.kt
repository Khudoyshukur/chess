package uz.safix.chess.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import uz.safix.chess.ui.theme.ShaxmatTheme
import uz.safix.chess.ui.util.APP_URL
import uz.safix.chess.ui.util.MY_TG_URL
import uz.safix.chess.ui.util.tryOpenUrl

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShaxmatTheme {
                ChessApp(
                    onRateApp = { tryOpenUrl(APP_URL) },
                    onContactAuthor = { tryOpenUrl(MY_TG_URL) },
                    onExitApp = { this.finish() },
                )
            }
        }
    }
}