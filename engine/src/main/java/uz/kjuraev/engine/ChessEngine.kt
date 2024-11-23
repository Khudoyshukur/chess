package uz.kjuraev.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile

/**
 * Created by: androdev
 * Date: 23-11-2024
 * Time: 11:49 PM
 * Email: Kjuraev.001@mail.ru
 */

typealias Fen = String

interface ChessEngine {
    val stateStream: StateFlow<State>

    suspend fun start(params: ChessEngineParams)
    suspend fun startAndAwaitReady(params: ChessEngineParams)
    suspend fun stop()
    suspend fun getMove(fen: Fen): Fen

    sealed interface State {
        object Uninitialized: State
        data class Ready(val params: ChessEngineParams): State
    }
}

suspend fun StateFlow<ChessEngine.State>.awaitReady() {
    takeWhile { it !is ChessEngine.State.Ready }.collect()
}

suspend fun MutableStateFlow<ChessEngine.State>.moveToState(state: ChessEngine.State) {
    this.emit(state)
}