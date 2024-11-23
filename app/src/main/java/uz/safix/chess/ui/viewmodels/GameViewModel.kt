package uz.safix.chess.ui.viewmodels

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.game.GameResult
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kjuraev.engine.DifficultyLevel
import uz.safix.chess.model.ChessPiece
import uz.safix.chess.model.defaultBoardState
import uz.safix.chess.model.toBoardSquareState
import uz.safix.chess.service.ChessEngineService
import javax.inject.Inject

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 1:45 sPM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val side: Side = Side.valueOf(checkNotNull(savedStateHandle["side"]))
    val level = DifficultyLevel.valueOf(checkNotNull(savedStateHandle["level"]))
    val userPlayingWithWhite: Boolean get() = side == Side.WHITE

    private val _showPromotionDialogForIndex = MutableStateFlow<Int?>(null)
    val showPromotionDialogForIndex get() = _showPromotionDialogForIndex.asStateFlow()

    private val _squareStatesStream = MutableStateFlow(defaultBoardState)
    private val _selectedSquareIndexStream = MutableStateFlow<Int?>(null)
    private val _lastMoveStream = MutableStateFlow<Move?>(null)
    private val _kingAttackedIndexStream = MutableStateFlow<Int?>(null)

    val squareStatesStream = combine(
        _squareStatesStream,
        _selectedSquareIndexStream,
        _lastMoveStream,
        _kingAttackedIndexStream,
    ) { squareStates, selectedSquareIndex, lastMove, kingAttackedIndex ->
        squareStates.mapIndexed { index, square ->
            square.copy(
                isSelectedForMove = selectedSquareIndex == index,
                isKingAttacked = kingAttackedIndex == index,
                movedFrom = lastMove?.from?.ordinal == index,
                movedTo = lastMove?.to?.ordinal == index
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, defaultBoardState)

    private val _gameResultStream = MutableStateFlow(GameResult.ONGOING)
    val gameResultStream get() = _gameResultStream.asStateFlow()

    private val messengerStream = MutableStateFlow<Messenger?>(null)
    private val botMoves = MutableSharedFlow<String>()

    private val messengerHandler = Handler(Looper.getMainLooper()) {
        val move = it.data.getString(ChessEngineService.BUNDLE_MOVE)
        viewModelScope.launch { move?.let { moveByEngine(move) } }
        return@Handler true
    }

    private var botStopTrackerJob: Job? = null

    private val board = Board()

    init {
        initGame()
    }

    private fun initGame() = viewModelScope.launch {
        updateStateFromBoard()

        if (board.sideToMove == side.flip()) {
            enqueueEngineMove()
            initBotStopTracker()
        }
    }

    private fun initBotStopTracker() {
        botStopTrackerJob?.cancel()
        botStopTrackerJob = viewModelScope.launch {
            while (true) {
                delay(5000)
                if (board.sideToMove == side.flip()) {
                    enqueueEngineMove()
                }
            }
        }
    }

    private suspend fun updateStateFromBoard() {
        _squareStatesStream.emit(
            board.boardToArray().take(64).mapIndexed { index, piece ->
                piece.toBoardSquareState(index)
            }
        )

        if (board.backup.isNotEmpty()) {
            board.backup.last?.move?.let { _lastMoveStream.emit(it) }
        }
        if (board.isKingAttacked) {
            _kingAttackedIndexStream.emit(board.getKingSquare(board.sideToMove).ordinal)
        } else {
            _kingAttackedIndexStream.emit(null)
        }
    }

    private fun availableForPromotion(indexToClick: Int): Boolean {
        val selectedIndex = _selectedSquareIndexStream.value ?: return false
        if (selectedIndex !in 0..63) return false

        val squareState = squareStatesStream.value[selectedIndex]
        if (squareState.piece == null || squareState.piece !in setOf(ChessPiece.WhitePawn, ChessPiece.BlackPawn)) return false
        if (squareState.piece.side != side) return false

        return when (side) {
            Side.WHITE -> {
                return selectedIndex in 48..55 && indexToClick in 56..63
            }

            Side.BLACK -> {
                return selectedIndex in 8..15 && indexToClick in 0..7
            }
        }
    }

    fun onClick(clickedIndex: Int, promotion: PieceType?) = viewModelScope.launch {
        if (promotion == null && availableForPromotion(clickedIndex)) {
            _showPromotionDialogForIndex.emit(clickedIndex)
            return@launch
        }

        if (promotion != null) _showPromotionDialogForIndex.emit(null)

        if (board.isMated || board.isDraw) return@launch
        if (clickedIndex !in 0..63) return@launch
        if (board.sideToMove != side) return@launch

        _lastMoveStream.emit(null)

        val currentSelectedIndex = _selectedSquareIndexStream.value
        if (clickedIndex == currentSelectedIndex) {
            _selectedSquareIndexStream.emit(null)
            return@launch
        }

        val squareState = squareStatesStream.value[clickedIndex]
        if (currentSelectedIndex == null) {
            if (squareState.piece != null) {
                _selectedSquareIndexStream.emit(clickedIndex)
            }

            return@launch
        }

        if (
            squareState.piece?.side != null &&
            squareState.piece.side == squareStatesStream.value[currentSelectedIndex].piece?.side
        ) {
            _selectedSquareIndexStream.emit(clickedIndex)
            return@launch
        }

        val move = if (promotion == null) {
            Move(
                Square.values()[currentSelectedIndex],
                Square.values()[clickedIndex]
            )
        } else {
            Move(
                Square.values()[currentSelectedIndex],
                Square.values()[clickedIndex],
                Piece.make(side, promotion)
            )
        }

        if (move in board.legalMoves()) {
            val moved = board.doMove(move)
            if (moved) {
                updateStateFromBoard()
            }
        }
        _selectedSquareIndexStream.emit(null)

        if (board.sideToMove == side.flip() && !board.isMated && !board.isDraw) {
            enqueueEngineMove()
            initBotStopTracker()
        }

        checkGameOver()
    }

    private suspend fun checkGameOver() {
        if (board.isDraw) {
            _gameResultStream.emit(GameResult.DRAW)
        } else if (board.isMated) {
            when (board.sideToMove) {
                Side.WHITE -> _gameResultStream.emit(GameResult.BLACK_WON)
                Side.BLACK -> _gameResultStream.emit(GameResult.WHITE_WON)
                null -> _gameResultStream.emit(GameResult.BLACK_WON)
            }
        }
    }

    private suspend fun enqueueEngineMove() {
        val messenger = messengerStream.filterNotNull().first()
        getEngineMove(messenger, board.fen)
    }

    private suspend fun moveByEngine(move: String) {
        val newMove = Move(move, side.flip())

        if (newMove in board.legalMoves() && board.sideToMove == side.flip()) {
            val moved = board.doMove(newMove)
            if (moved) {
                updateStateFromBoard()
            }
        }

        checkGameOver()
    }

    private suspend fun getEngineMove(messenger: Messenger, fen: String): String? {
        val message = Message.obtain(null, ChessEngineService.MSG_GET_MOVE)
        message.data = bundleOf(ChessEngineService.BUNDLE_FEN to fen)
        message.replyTo = Messenger(messengerHandler)
        messenger.send(message)

        return botMoves.firstOrNull()
    }

    fun setMessenger(messenger: Messenger?) = viewModelScope.launch {
        messengerStream.emit(messenger)
    }

    companion object {
        private val bestStartingMoves =
            listOf("e2e4", "d2d4", "g1f3", "c2c4", "b1c3", "b2b3", "g2g3")
    }
}