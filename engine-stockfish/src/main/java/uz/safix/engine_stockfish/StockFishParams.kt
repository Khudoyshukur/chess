package uz.safix.engine_stockfish

import androidx.annotation.IntRange
import uz.kjuraev.engine.ChessEngineParams
import uz.kjuraev.engine.DifficultyLevel

internal data class StockFishParams(
    @IntRange(from = 0, to = 20) val skillLevel: Int,
    val depth: Int,
)

internal fun ChessEngineParams.toStockfishParams(): StockFishParams {
    return when (this.difficultyLevel) {
        DifficultyLevel.NOVICE -> StockFishParams(0, 1)
        DifficultyLevel.BEGINNER -> StockFishParams(3, 2)
        DifficultyLevel.PRE_INTERMEDIATE -> StockFishParams(5, 3)
        DifficultyLevel.INTERMEDIATE -> StockFishParams(7, 4)
        DifficultyLevel.UPPER_INTERMEDIATE -> StockFishParams(9, 5)
        DifficultyLevel.ADVANCED -> StockFishParams(11, 6)
        DifficultyLevel.EXPERT -> StockFishParams(20, 10)
    }
}
