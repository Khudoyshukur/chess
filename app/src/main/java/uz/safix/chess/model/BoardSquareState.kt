package uz.safix.chess.model

import androidx.compose.runtime.Stable

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 1:10 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Stable
data class BoardSquareState(
    val piece: ChessPiece?,
    val index: Int,
    val isSelectedForMove: Boolean = false,
    val isKingAttacked: Boolean = false,
    val movedFrom: Boolean = false,
    val movedTo: Boolean = false,
    val isPossibleMove: Boolean = false,
)