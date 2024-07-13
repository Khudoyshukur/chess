package uz.safix.chess.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:02 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Parcelize
enum class DifficultyLevel(val depth: Int): Parcelable {
    EASY(3), MIDDLE(6), HARD(12), EXTRA_HARD(20)
}