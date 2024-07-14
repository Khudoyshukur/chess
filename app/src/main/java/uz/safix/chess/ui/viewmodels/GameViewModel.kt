package uz.safix.chess.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.safix.chess.model.DifficultyLevel
import uz.safix.chess.model.defaultBoardState
import uz.safix.chess.model.toBoardSquareState
import uz.safix.engine_lc0.FenParam
import uz.safix.engine_lc0.Lc0Engine
import javax.inject.Inject

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 1:45 sPM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@HiltViewModel
class GameViewModel @Inject constructor(
    private val engine: Lc0Engine,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val level = DifficultyLevel.valueOf(checkNotNull(savedStateHandle["level"]))
    private val side: Side = Side.valueOf(checkNotNull(savedStateHandle["side"]))
    val userPlayingWithWhite: Boolean get() = side == Side.WHITE

    private val _squareStatesStream = MutableStateFlow(defaultBoardState)
    val squareStatesStream get() = _squareStatesStream.asStateFlow()

    private val _selectedSquareIndexStream = MutableStateFlow<Int?>(null)
    val selectedSquareIndexStream get() = _selectedSquareIndexStream.asStateFlow()

    private val _lastMoveStream = MutableStateFlow<Move?>(null)
    val lastMoveStream get() = _lastMoveStream.asStateFlow()

    private val _kingAttackedIndexStream = MutableStateFlow<Int?>(null)
    val kingAttackedIndexStream get() = _kingAttackedIndexStream.asStateFlow()

    private val board = Board()

    init {
        initGame()
    }

    private fun initGame() = viewModelScope.launch {
        updateStateFromBoard()
        engine.init(level.weights)

        if (side == Side.BLACK) {
            moveByEngine(bestStartingMoves.random())
        }

        engine.startAndWait()
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

    fun onClick(clickedIndex: Int) = viewModelScope.launch {
        if (clickedIndex !in 0..63) {
            return@launch
        }

        if (board.sideToMove != side) {
            return@launch
        }

        _lastMoveStream.emit(null)

        val currentSelectedIndex = selectedSquareIndexStream.value
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

        val move = Move(
            Square.values()[currentSelectedIndex],
            Square.values()[clickedIndex]
        )

        if (move in board.legalMoves()) {
            val moved = board.doMove(move)
            if (moved) {
                updateStateFromBoard()
            }
        }
        _selectedSquareIndexStream.emit(null)

        if (board.sideToMove == side.flip()) {
            moveByEngine()
        }
    }

    private suspend fun moveByEngine(move: String? = null) {
        val currentTime = System.currentTimeMillis()
        val engineMove = move ?: engine.getMove(FenParam(board.fen))
        val newMove = Move(engineMove, side.flip())
        val diff = System.currentTimeMillis() - currentTime

        if (diff < COMPUTER_THINKING_TIME_MILLIS) {
            delay(COMPUTER_THINKING_TIME_MILLIS - diff)
        }

        if (newMove in board.legalMoves()) {
            val moved = board.doMove(newMove)
            if (moved) {
                updateStateFromBoard()
            }
        }
    }

    companion object {
        private const val COMPUTER_THINKING_TIME_MILLIS = 1_000 // 1 second
        private val bestStartingMoves = listOf("e2e4", "d2d4", "g1f3", "c2c4", "b1c3", "b2b3", "g2g3")
    }
}