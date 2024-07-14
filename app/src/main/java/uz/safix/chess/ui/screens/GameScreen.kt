package uz.safix.chess.ui.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Messenger
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bhlangonijr.chesslib.game.GameResult
import uz.safix.chess.R
import uz.safix.chess.service.ChessEngineService
import uz.safix.chess.ui.components.ChessBoard
import uz.safix.chess.ui.components.ChoosePromotionDialog
import uz.safix.chess.ui.components.GameResultDialog
import uz.safix.chess.ui.viewmodels.GameViewModel

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:25 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    onFinished: () -> Unit = {}
) {
    val squareStates by viewModel.squareStatesStream.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedSquareIndexStream.collectAsStateWithLifecycle()
    val lastEngineMove by viewModel.lastMoveStream.collectAsStateWithLifecycle()
    val attackedKingIndex by viewModel.kingAttackedIndexStream.collectAsStateWithLifecycle()
    val gameResult by viewModel.gameResultStream.collectAsStateWithLifecycle()
    var showPromotionDialogForIndex by remember { mutableStateOf<Int?>(null) }

    var messenger: Messenger? by remember { mutableStateOf(null) }
    val connection = remember { object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            messenger = Messenger(binder)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            messenger = null
        }
    } }

    // Bind on composition and unbind on disposal
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val intent = ChessEngineService.getBoundIntent(context, viewModel.level.weights)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(connection) }
    }

    messenger?.let { viewModel.setMessenger(messenger) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.home_background)
    ) {
        ChessBoard(
            states = squareStates,
            userPlayingWithWhite = viewModel.userPlayingWithWhite,
            selectedIndex = selectedIndex,
            attackedKingIndex = attackedKingIndex,
            lastEngineMove = lastEngineMove,
            onClick = { index ->
                if (viewModel.availableForPromotion(index)) {
                    showPromotionDialogForIndex = index
                } else {
                    viewModel.onClick(index, null)
                }
            }
        )
    }

    if (gameResult != GameResult.ONGOING) {
        GameResultDialog(gameResult = gameResult) {
            onFinished()
        }
    }

    if (showPromotionDialogForIndex != null) {
        ChoosePromotionDialog(
            onOptionSelected = { promotion ->
                showPromotionDialogForIndex?.let { viewModel.onClick(it, promotion) }
                showPromotionDialogForIndex = null
            }
        )
    }
}

@Preview
@Composable
fun PlayWithComputerPreview() {
    GameScreen()
}