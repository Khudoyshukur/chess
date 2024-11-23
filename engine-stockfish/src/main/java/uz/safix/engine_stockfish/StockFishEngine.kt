package uz.safix.engine_stockfish

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import uz.kjuraev.engine.ChessEngine
import uz.kjuraev.engine.ChessEngineParams
import uz.kjuraev.engine.Fen
import uz.kjuraev.engine.awaitReady
import uz.kjuraev.engine.moveToState
import kotlin.coroutines.CoroutineContext

internal class StockFishEngine(
    private val bridge: StockfishJni,
    private val engineContext: CoroutineContext,
): ChessEngine {
    private val _stateStream = MutableStateFlow<ChessEngine.State>(ChessEngine.State.Uninitialized)
    override val stateStream: StateFlow<ChessEngine.State>
        get() = _stateStream.asStateFlow()

    private val coroutineScope = CoroutineScope(engineContext)
    private var moveListenerJob: Job? = null
    private var moveCompletable: CompletableDeferred<String>? = null
    private val synchronizer = Mutex()

    private lateinit var currentParams: StockFishParams

    init {
        logEvent("Init")
        bridge.init()
    }

    override suspend fun startAndAwaitReady(params: ChessEngineParams) {
        logEvent("StartAndAwaitReady $params")

        start(params)
        stateStream.awaitReady()
    }

    override suspend fun start(params: ChessEngineParams) = synchronizer.withLock {
        currentParams = params.toStockfishParams()

        moveListenerJob?.cancel()
        moveListenerJob = coroutineScope.launch {
            launch {
                logEvent("start main")
                bridge.main(
                    threadCount = DEFAULT_THREAD_COUNT,
                    skillLevel = currentParams.skillLevel
                )
            }

            launch {
                logEvent("start listening moves")
                while (isActive) {
                    val output = bridge.readLine() ?: continue

                    if (output.startsWith(INIT_TOKEN)) {
                        logEvent("Ready")
                        _stateStream.moveToState(ChessEngine.State.Ready(params))
                    } else if (output.startsWith(BEST_MOVE_TOKEN)) {
                        // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        val move = output.split(" ")[1].trim()
                        moveCompletable?.complete(move)
                    }
                }
            }
        }
    }

    override suspend fun stop() {
        logEvent("stop")

        moveListenerJob?.cancel()
        bridge.writeLine(EngineCommand.Quit.toString())
        _stateStream.moveToState(ChessEngine.State.Uninitialized)
    }

    override suspend fun getMove(fen: Fen): Fen = withContext(engineContext) {
        logEvent("getMove $fen")

        stateStream.awaitReady()
        moveCompletable = CompletableDeferred()

        bridge.writeLine(EngineCommand.SetPosition(fen).toString())
        bridge.writeLine(EngineCommand.GoDepth(currentParams.depth).toString())

        moveCompletable!!.await().also {
            logEvent("getMove resp $it")
        }
    }

    private fun logEvent(message: String) {
        Log.d("StockFishEngine", message)
    }

    companion object {
        internal const val INIT_TOKEN = "Stockfish"
        internal const val BEST_MOVE_TOKEN = "bestmove"
        private const val DEFAULT_THREAD_COUNT = 4
    }
}
