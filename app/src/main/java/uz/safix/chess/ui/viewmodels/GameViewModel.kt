package uz.safix.chess.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.safix.chess.model.BoardSquareState
import uz.safix.chess.model.defaultBoardState
import uz.safix.chess.model.toBoardSquareState
import uz.safix.engine_stockfish.FenAndDepth
import uz.safix.engine_stockfish.StockFishEngine
import javax.inject.Inject

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 1:45 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@HiltViewModel
class GameViewModel @Inject constructor(
    private val engine: StockFishEngine
) : ViewModel() {
    private val _gameState = MutableStateFlow(defaultBoardState)
    private val selectedSquareIndex = MutableStateFlow<Int?>(null)

    val gameState
        get() = _gameState.combine(selectedSquareIndex) { gameState, selectedIndex ->
            gameState.mapIndexed { index, squareState ->
                squareState.copy(isSelectedForMove = index == selectedIndex)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = defaultBoardState
        )

    private val board = Board()

    init {
        initGame()
    }

    private fun initGame() = viewModelScope.launch {
        updateStateFromBoard()
        engine.init(Unit)
        engine.startAndWait()
    }

    private suspend fun updateStateFromBoard() {
        _gameState.emit(
            board.boardToArray().take(64).mapIndexed { index, piece ->
                piece.toBoardSquareState(index == selectedSquareIndex.value, index)
            }
        )
    }

    fun onClick(clickedIndex: Int) = viewModelScope.launch {
        if (clickedIndex !in 0..63) {
            return@launch
        }

        val currentSelectedIndex = selectedSquareIndex.value
        if (clickedIndex == currentSelectedIndex) {
            selectedSquareIndex.emit(null)
            return@launch
        }

        val squareState = _gameState.value[clickedIndex]
        if (currentSelectedIndex == null) {
            if (squareState.piece != null) {
                selectedSquareIndex.emit(clickedIndex)
            }

            return@launch
        }


        if (
            squareState.piece?.side != null &&
            squareState.piece.side == _gameState.value[currentSelectedIndex].piece?.side
        ) {
            selectedSquareIndex.emit(clickedIndex)
            return@launch
        }

        val legalMove = board.doMove(
            Move(Square.values()[currentSelectedIndex], Square.values()[clickedIndex]),
            true
        )
        if (legalMove) {
            updateStateFromBoard()
        }
        selectedSquareIndex.emit(null)

        if (board.sideToMove == Side.BLACK) {
            moveByEngine()
        }
    }

    private suspend fun moveByEngine() {
        val currentTime = System.currentTimeMillis()
        val newMove = engine.getMove(FenAndDepth(board.fen, 10))
        val diff = System.currentTimeMillis() - currentTime

        if (diff < 2_000) {
            delay(2_000 - diff)
        }

        val legalMove = board.doMove(newMove)
        if (legalMove) {
            updateStateFromBoard()
        }
    }
}