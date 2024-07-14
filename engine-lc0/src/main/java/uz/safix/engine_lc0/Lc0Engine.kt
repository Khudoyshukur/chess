package uz.safix.engine_lc0

import android.content.Context
import androidx.annotation.Keep
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class Lc0Engine @Inject constructor(
    private val bridge: Lc0Jni,
    @ApplicationContext
    private val context: Context,
    private val engineContext: CoroutineContext,
) {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)
    private lateinit var weightsFile: File

    fun init(params: MaiaWeights) {
        val weightFileName = if (params.asset.contains('/')) {
            params.asset.substring(params.asset.indexOf('/') + 1)
        } else {
            params.asset
        }
        weightsFile = File(context.dataDir, weightFileName)

        if (!weightsFile.exists()) {
            context.assets.open(params.asset).copyTo(
                weightsFile.outputStream(),
            )
        }
        bridge.init()
    }

    suspend fun awaitReady() {
        awaitState<State.Ready>()
    }

    suspend fun startAndWait() {
        awaitState<State.Uninitialized>()
        CoroutineScope(coroutineContext).launch {
            launch(engineContext) {
                bridge.main(weightsFile.absolutePath)
            }

            launch(engineContext) {
                while (isActive) {
                    val output = bridge.readLine() ?: continue
                    if (output.startsWith(BEST_MOVE_TOKEN)) {
                        // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        val move = output.split(" ")[1].trim()
                        assertStateOrNull<State.Moving>()?.completable?.complete(move)
                    }
                }
            }
            moveToState(State.Ready)
        }.let { job ->
            try {
                awaitCancellation()
            } finally {
                job.cancel()
                moveToState(State.Uninitialized)
            }
        }
    }

    suspend fun getMove(params: FenParam): String {
        return withContext(engineContext) {
            awaitState<State.Ready>()
            val moveCompletable = CompletableDeferred<String>()
            moveToState(State.Moving(moveCompletable))

            bridge.writeLine(EngineCommand.SetPosition(params.fen).toString())
            bridge.writeLine(EngineCommand.GoNodes.toString())

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
        internal const val BEST_MOVE_TOKEN = "bestmove"
    }
}
