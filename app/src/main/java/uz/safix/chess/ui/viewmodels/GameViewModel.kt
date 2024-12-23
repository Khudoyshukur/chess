package uz.safix.chess.ui.viewmodels

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kjuraev.engine.DifficultyLevel
import uz.safix.chess.R
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

private val castleMoves = setOf("e1g1", "e1c1", "e8g8", "e8c8")

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
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
    private val possibleMovesStream: Flow<Set<Int>> = _selectedSquareIndexStream
        .combine(_lastMoveStream, ::Pair)
        .map { (index, _) ->
            if (index == null) {
                setOf()
            } else {
                board.legalMoves().filter { it.from.ordinal == index }
                    .map { it.to.ordinal }
                    .toSet()
            }
        }

    val squareStatesStream = combine(
        _squareStatesStream,
        _selectedSquareIndexStream,
        _lastMoveStream,
        _kingAttackedIndexStream,
        possibleMovesStream
    ) { squareStates, selectedSquareIndex, lastMove, kingAttackedIndex, possibleMoves ->
        squareStates.mapIndexed { index, square ->
            square.copy(
                isSelectedForMove = selectedSquareIndex == index,
                isKingAttacked = kingAttackedIndex == index,
                movedFrom = lastMove?.from?.ordinal == index,
                movedTo = lastMove?.to?.ordinal == index,
                isPossibleMove = possibleMoves.contains(index)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, defaultBoardState)

    private val _gameResultStream = MutableStateFlow(GameResult.ONGOING)
    val gameResultStream get() = _gameResultStream.asStateFlow()

    private val messengerStream = MutableStateFlow<Messenger?>(null)
    private val messengerHandler = Handler(Looper.getMainLooper()) {
        val move = it.data.getString(ChessEngineService.BUNDLE_MOVE)
        viewModelScope.launch { move?.let { moveByEngine(move) } }
        return@Handler true
    }

    private var botStopTrackerJob: Job? = null

    private val board = Board()

    init {
        initGame()
        playStartGame()
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
        if (squareState.piece == null || squareState.piece !in setOf(
                ChessPiece.WhitePawn,
                ChessPiece.BlackPawn
            )
        ) return false
        if (squareState.piece.side != side) return false

        return when (side) {
            Side.WHITE -> {
                selectedIndex in 48..55 && indexToClick in 56..63
            }

            Side.BLACK -> {
                selectedIndex in 8..15 && indexToClick in 0..7
            }
        }
    }

    fun onClick(clickedIndex: Int, promotion: PieceType?) = viewModelScope.launch {
        if (promotion == null && availableForPromotion(clickedIndex)) {
            _showPromotionDialogForIndex.emit(clickedIndex)
            return@launch
        }
        if (promotion != null) _showPromotionDialogForIndex.emit(null)

        if (clickedIndex !in 0..63) return@launch

        val currentSelectedIndex = _selectedSquareIndexStream.value
        if (clickedIndex == currentSelectedIndex) {
            _selectedSquareIndexStream.emit(null)
            return@launch
        }

        val squareState = squareStatesStream.value[clickedIndex]
        if (
            currentSelectedIndex == null ||
            squareStatesStream.value.getOrNull(currentSelectedIndex)?.piece == null
        ) {
            _selectedSquareIndexStream.emit(clickedIndex)
            return@launch
        }

        if (board.isMated || board.isDraw) return@launch
        if (board.sideToMove != side) return@launch

        _lastMoveStream.emit(null)

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
                val movingPiece = squareStatesStream.value[move.from.ordinal].piece
                playMoveAudio(
                    isCaptured = squareState.piece != null,
                    isCastle = move.toString() in castleMoves && movingPiece in setOf(
                        ChessPiece.WhiteKing,
                        ChessPiece.BlackKing
                    ),
                    isPromote = move.promotion != Piece.NONE
                )

                updateStateFromBoard()
            }
        } else {
            playIllegalMove()
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

    private suspend fun moveByEngine(move: String) = try {
        val newMove = Move(move, side.flip())

        if (newMove in board.legalMoves() && board.sideToMove == side.flip()) {
            val moved = board.doMove(newMove)
            if (moved) {
                val movingPiece = squareStatesStream.value[newMove.from.ordinal].piece
                playMoveAudio(
                    isCaptured = squareStatesStream.value[newMove.to.ordinal].piece != null,
                    isCastle = newMove.toString() in castleMoves && movingPiece in setOf(
                        ChessPiece.WhiteKing,
                        ChessPiece.BlackKing
                    ),
                    isPromote = newMove.promotion != Piece.NONE
                )
                updateStateFromBoard()
            }
        }

        checkGameOver()
    } catch (t: Throwable) {
        t.printStackTrace()
    }

    private fun getEngineMove(messenger: Messenger, fen: String) {
        val message = Message.obtain(null, ChessEngineService.MSG_GET_MOVE)
        message.data = bundleOf(ChessEngineService.BUNDLE_FEN to fen)
        message.replyTo = Messenger(messengerHandler)
        messenger.send(message)
    }

    fun setMessenger(messenger: Messenger?) = viewModelScope.launch {
        messengerStream.emit(messenger)
    }

    private fun playMoveAudio(
        isCaptured: Boolean,
        isCastle: Boolean,
        isPromote: Boolean
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val audio =
                if (board.isMated || board.isStaleMate || board.isRepetition || board.isDraw) {
                    R.raw.game_end
                } else if (board.isKingAttacked) {
                    R.raw.move_check
                } else if (isCaptured) {
                    R.raw.capture
                } else if (isCastle) {
                    R.raw.castle
                } else if (isPromote) {
                    R.raw.promote
                } else {
                    R.raw.move
                }
            MediaPlayer.create(appContext, audio).also { player ->
                player.start()
                player.setOnCompletionListener {
                    it.release()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun playStartGame() = viewModelScope.launch(Dispatchers.IO) {
        try {
            MediaPlayer.create(appContext, R.raw.game_start).also { player ->
                player.start()
                player.setOnCompletionListener {
                    it.release()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun playIllegalMove() = viewModelScope.launch(Dispatchers.IO) {
        try {
            MediaPlayer.create(appContext, R.raw.illegal).also { player ->
                player.start()
                player.setOnCompletionListener {
                    it.release()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    companion object {
//        private val bestStartingMoves =
//            listOf("e2e4", "d2d4", "g1f3", "c2c4", "b1c3", "b2b3", "g2g3")
    }
}