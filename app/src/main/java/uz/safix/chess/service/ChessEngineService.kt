package uz.safix.chess.service

//import android.app.Service
//import android.content.Intent
//import android.os.IBinder
//import android.os.Process
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import uz.safix.engine_stockfish.StockFishEngine
//import javax.inject.Inject
//
///**
// * Created by: androdev
// * Date: 13-07-2024
// * Time: 6:25 PM
// * Email: Khudoyshukur.Juraev.001@mail.ru
// */
//
//@AndroidEntryPoint
//class ChessEngineService: Service() {
//    private var engineJob: Job? = null
//
//    @Inject lateinit var engine: StockFishEngine
//
//    override fun onCreate() {
//        super.onCreate()
//        engineJob = CoroutineScope(Dispatchers.Default).launch {
//            with(engine) {
//                init(initParams())
//                startAndWait()
//            }
//        }
//    }
//
//    override fun onUnbind(intent: Intent?): Boolean {
//        stopSelf()
//        return super.onUnbind(intent)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        println("onDestroy")
//        Process.killProcess(Process.myPid())
//        engineJob?.cancel()
//    }
//
////    abstract suspend fun initParams(): EngineInit
////
////    abstract fun engine(): ChessEngine<EngineInit, Move>
////    override fun onBind(intent: Intent?): IBinder {
////        return object : AnalyzerEngineInterface.Stub() {
////            override fun bestMove(fen: String, depth: Int): String {
////                return runBlocking {
////                    engine().getMove(FenAndDepth(fen, depth))
////                }
////            }
////        }
////    }
//}