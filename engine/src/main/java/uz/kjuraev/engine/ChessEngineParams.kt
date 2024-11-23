package uz.kjuraev.engine

/**
 * Created by: androdev
 * Date: 23-11-2024
 * Time: 11:50 PM
 * Email: Kjuraev.001@mail.ru
 */

enum class DifficultyLevel {
    NOVICE,
    BEGINNER,
    PRE_INTERMEDIATE,
    INTERMEDIATE,
    UPPER_INTERMEDIATE,
    ADVANCED,
    EXPERT
}

data class ChessEngineParams(val difficultyLevel: DifficultyLevel)

