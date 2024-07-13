package uz.safix.chess.model

import androidx.annotation.DrawableRes
import uz.safix.chess.R

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:53 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

enum class ChessPiece {
    WhiteKing,
    WhiteQueen,
    WhiteRook,
    WhiteBishop,
    WhiteKnight,
    WhitePawn,

    BlackKing,
    BlackQueen,
    BlackRook,
    BlackBishop,
    BlackKnight,
    BlackPawn
}

@get:DrawableRes
val ChessPiece.getDrawable get() = when(this) {
    ChessPiece.WhiteKing -> R.drawable.piece_king_white
    ChessPiece.WhiteQueen -> R.drawable.piece_queen_white
    ChessPiece.WhiteRook -> R.drawable.piece_rook_white
    ChessPiece.WhiteBishop -> R.drawable.piece_bishop_white
    ChessPiece.WhiteKnight -> R.drawable.piece_knight_white
    ChessPiece.WhitePawn -> R.drawable.piece_pawn_white
    ChessPiece.BlackKing -> R.drawable.piece_king_black
    ChessPiece.BlackQueen -> R.drawable.piece_queen_black
    ChessPiece.BlackRook -> R.drawable.piece_rook_black
    ChessPiece.BlackBishop -> R.drawable.piece_bishop_black
    ChessPiece.BlackKnight -> R.drawable.piece_knight_black
    ChessPiece.BlackPawn -> R.drawable.piece_pawn_black
}

val defaultBoardState = List(64) {
    when(it) {
        0 -> ChessPiece.WhiteRook
        1 -> ChessPiece.WhiteKnight
        2 -> ChessPiece.WhiteBishop
        3 -> ChessPiece.WhiteQueen
        4 -> ChessPiece.WhiteKing
        5 -> ChessPiece.WhiteBishop
        6 -> ChessPiece.WhiteKnight
        7 -> ChessPiece.WhiteRook
        in 8..15 -> ChessPiece.WhitePawn

        in 48..55 -> ChessPiece.BlackPawn
        56 -> ChessPiece.BlackRook
        57 -> ChessPiece.BlackKing
        58 -> ChessPiece.BlackBishop
        59 -> ChessPiece.BlackQueen
        60 -> ChessPiece.BlackKing
        61 -> ChessPiece.BlackBishop
        62 -> ChessPiece.BlackKnight
        63 -> ChessPiece.BlackRook


        else -> null
    }
}