package uz.safix.chess.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.Process
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uz.kjuraev.engine.ChessEngine
import uz.kjuraev.engine.ChessEngineParams
import uz.kjuraev.engine.DifficultyLevel
import uz.safix.engine_stockfish.ChessEngineStockfish
import javax.inject.Inject

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 6:25 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@AndroidEntryPoint
class ChessEngineService: Service() {
    private var engineJob: Job? = null

    @ChessEngineStockfish
    @Inject lateinit var engine: ChessEngine

     private val difficultyLevelCompletable = CompletableDeferred<DifficultyLevel>()

    override fun onCreate() {
        super.onCreate()
        engineJob = CoroutineScope(Dispatchers.Default).launch {
            with(engine) {
                val difficultyLevel = difficultyLevelCompletable.await()
                startAndAwaitReady(ChessEngineParams(difficultyLevel))
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        difficultyLevelCompletable.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        runBlocking(Dispatchers.IO) { engine.stop() }
        Process.killProcess(Process.myPid())
        engineJob?.cancel()
    }

    private val incomingMessageHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_GET_MOVE -> {
                    runBlocking(Dispatchers.IO) {
                        val startTime = System.currentTimeMillis()

                        val replyMessage = Message.obtain(null, MSG_GET_MOVE)
                        val fen = msg.data.getString(BUNDLE_FEN)
                        val reply = if (fen != null) engine.getMove(fen) else ""
                        replyMessage.data = bundleOf(BUNDLE_MOVE to reply)

                        val diff = System.currentTimeMillis() - startTime
                        if (diff < COMPUTER_THINKING_TIME_MILLIS) {
                            delay(COMPUTER_THINKING_TIME_MILLIS - diff)
                        }

                        msg.replyTo?.send(replyMessage)
                    }
                }
            }
        }
    }

    private val messenger = Messenger(incomingMessageHandler)

    override fun onBind(intent: Intent?): IBinder? {
        intent?.getStringExtra(EXTRA_DIFFICULTY_LEVEL)?.let {
            difficultyLevelCompletable.complete(DifficultyLevel.valueOf(it))
        } ?: throw IllegalArgumentException("Difficulty level should be provided")

        return messenger.binder
    }

    companion object {
        private const val COMPUTER_THINKING_TIME_MILLIS = 1_500 // 1.5 second
        private const val EXTRA_DIFFICULTY_LEVEL = "EXTRA_WEIGHT"
        const val BUNDLE_FEN = "BUNDLE_FEN"
        const val BUNDLE_MOVE = "BUNDLE_MOVE"
        const val MSG_GET_MOVE = 1

        fun getBoundIntent(context: Context, difficultyLevel: DifficultyLevel): Intent {
            return Intent(context, ChessEngineService::class.java).also {
                 it.putExtra(EXTRA_DIFFICULTY_LEVEL, difficultyLevel.name)
            }
        }
    }
}