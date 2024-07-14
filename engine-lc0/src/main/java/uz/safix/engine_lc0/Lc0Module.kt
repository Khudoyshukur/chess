package uz.safix.engine_lc0

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Lc0Module {
    @Provides
    @Singleton
    internal fun lc0Jni(): Lc0Jni = Lc0JniImpl()

    @Provides
    @Singleton
    internal fun lc0Engine(
        bridge: Lc0Jni,
        @ApplicationContext context: Context,
    ): Lc0Engine {
        val coroutineContext = CoroutineName("Lc0") + Dispatchers.IO
        return Lc0Engine(bridge, context, coroutineContext)
    }
}
