package uz.safix.chess.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import uz.safix.engine_lc0.MaiaWeights

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:02 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Parcelize
enum class DifficultyLevel(val weights: MaiaWeights): Parcelable {
    EASY(MaiaWeights.ELO_1100),
    MIDDLE(MaiaWeights.ELO_1200),
    HARD(MaiaWeights.ELO_1400),
    EXTRA_HARD(MaiaWeights.ELO_1900)
}