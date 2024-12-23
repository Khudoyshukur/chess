package uz.safix.chess.ui.screens

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Messenger
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import uz.kjuraev.engine.DifficultyLevel
import uz.safix.chess.R
import uz.safix.chess.model.BoardSquareState
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
    val gameResult by viewModel.gameResultStream.collectAsStateWithLifecycle()
    val showPromotionDialogForIndex by viewModel.showPromotionDialogForIndex.collectAsStateWithLifecycle()
    val userPlayingWithWhite by remember { derivedStateOf { viewModel.userPlayingWithWhite } }
    val difficultyLevel by remember { derivedStateOf { viewModel.level } }
    val clickHandler: (Int) -> Unit = remember { { viewModel.onClick(it, null) } }
    var showGameEndDialog by remember { mutableStateOf(false) }

    MainScreen(
        squareStates = squareStates,
        userPlayingWithWhite = userPlayingWithWhite,
        difficultyLevel = difficultyLevel,
        setMessenger = viewModel::setMessenger,
        onClick = clickHandler
    )

    LaunchedEffect(gameResult != GameResult.ONGOING) {
        if (gameResult != GameResult.ONGOING) {
            showGameEndDialog = true
        }
    }

    if (showGameEndDialog) {
        GameResultDialog(gameResult = gameResult) {
            showGameEndDialog = false
            onFinished()
        }
    }

    if (showPromotionDialogForIndex != null) {
        ChoosePromotionDialog(
            onOptionSelected = { promotion ->
                showPromotionDialogForIndex?.let { viewModel.onClick(it, promotion) }
            }
        )
    }
}

@Composable
fun MainScreen(
    squareStates: List<BoardSquareState>,
    userPlayingWithWhite: Boolean,
    difficultyLevel: DifficultyLevel,
    setMessenger: (Messenger?) -> Unit,
    onClick: (Int) -> Unit = {}
) {
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                setMessenger(Messenger(binder))
            }

            override fun onServiceDisconnected(className: ComponentName) {
                setMessenger(null)
            }
        }
    }

    // Bind on composition and unbind on disposal
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val intent = ChessEngineService.getBoundIntent(context, difficultyLevel)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(connection) }
    }

    val bgImageRes = remember { R.color.home_background }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = bgImageRes)
    ) {
        ChessBoard(
            states = squareStates,
            userPlayingWithWhite = userPlayingWithWhite,
            onClick = onClick
        )
    }
}

@Preview
@Composable
fun PlayWithComputerPreview() {
    GameScreen()
}