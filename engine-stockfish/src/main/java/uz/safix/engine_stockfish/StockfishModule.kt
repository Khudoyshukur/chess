package uz.safix.engine_stockfish

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 3:41 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */


@Module
@InstallIn(SingletonComponent::class)
class StockfishModule {
    @Provides
    @Singleton
    internal fun stockfishJni(): StockfishJni = AndroidStockfishJni()

    @Provides
    @Singleton
    internal fun stockfishEngine(bridge: StockfishJni): StockFishEngine {
        val context = CoroutineName("Stockfish") + Dispatchers.IO
        return StockFishEngine(bridge, context)
    }
}