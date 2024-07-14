package uz.safix.chess.model

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 1:10 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

data class BoardSquareState(
    val piece: ChessPiece?,
    val index: Int
)