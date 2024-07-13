package uz.safix.engine_stockfish

import androidx.annotation.Keep
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class StockFishEngine(
    private val bridge: StockfishJni,
    private val engineContext: CoroutineContext,
) {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    fun init(params: Unit) {
        bridge.init()
    }

    suspend fun awaitReady() {
        awaitState<State.Ready>()
    }

    suspend fun startAndWait() {
        awaitState<State.Uninitialized>()
        CoroutineScope(coroutineContext).launch {
            launch(engineContext) {
                bridge.main(threadCount = DEFAULT_THREAD_COUNT)
            }

            launch(engineContext) {
                while (isActive) {
                    val output = bridge.readLine() ?: continue
                    if (output.startsWith(INIT_TOKEN)) {
                        moveToState(State.Ready)
                    } else if (output.startsWith(BEST_MOVE_TOKEN)) {
                        // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        val move = output.split(" ")[1].trim()
                        assertStateOrNull<State.Moving>()?.completable?.complete(move)
                    }
                }
            }
        }.let { job ->
            try {
                awaitCancellation()
            } finally {
                bridge.writeLine(EngineCommand.Quit.toString())
                job.cancel()
                moveToState(State.Uninitialized)
            }
        }
    }

    suspend fun getMove(params: FenAndDepth): String {
        val (fen, depth) = params
        return withContext(engineContext) {
            awaitState<State.Ready>()
            val moveCompletable = CompletableDeferred<String>()
            moveToState(State.Moving(moveCompletable))

            listOf(
                EngineCommand.SetPosition(fen),
                EngineCommand.GoDepth(depth),
            ).forEach {
                bridge.writeLine(it.toString())
            }

            moveCompletable.await().also {
                moveToState(State.Ready)
            }
        }
    }

    private inline fun <reified T : State> assertStateOrNull(): T? {
        return stateFlow.value as? T
    }

    private suspend inline fun <reified T : State> awaitState() {
        stateFlow.takeWhile { it !is T }.collect()
    }

    private suspend fun moveToState(state: State) {
        stateFlow.emit(state)
    }

    private sealed interface State {
        @Keep
        object Uninitialized : State

        @Keep
        class Moving(
            val completable: CompletableDeferred<String>,
        ) : State

        @Keep
        object Ready : State
    }

    companion object {
        internal const val INIT_TOKEN = "Stockfish"
        internal const val BEST_MOVE_TOKEN = "bestmove"
        private const val DEFAULT_THREAD_COUNT = 4
    }
}
