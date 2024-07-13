package uz.safix.chess.model

import androidx.annotation.DrawableRes
import com.github.bhlangonijr.chesslib.Piece
import uz.safix.chess.R

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:53 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

enum class Side {
    WHITE, BLACK
}

enum class ChessPiece(val side: Side) {
    WhiteKing(Side.WHITE),
    WhiteQueen(Side.WHITE),
    WhiteRook(Side.WHITE),
    WhiteBishop(Side.WHITE),
    WhiteKnight(Side.WHITE),
    WhitePawn(Side.WHITE),

    BlackKing(Side.BLACK),
    BlackQueen(Side.BLACK),
    BlackRook(Side.BLACK),
    BlackBishop(Side.BLACK),
    BlackKnight(Side.BLACK),
    BlackPawn(Side.BLACK)
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
        0 -> BoardSquareState(ChessPiece.WhiteRook, false, it)
        1 -> BoardSquareState(ChessPiece.WhiteKnight, false, it)
        2 -> BoardSquareState(ChessPiece.WhiteBishop, false, it)
        3 -> BoardSquareState(ChessPiece.WhiteQueen, false, it)
        4 -> BoardSquareState(ChessPiece.WhiteKing, false, it)
        5 -> BoardSquareState(ChessPiece.WhiteBishop, false, it)
        6 -> BoardSquareState(ChessPiece.WhiteKnight, false, it)
        7 -> BoardSquareState(ChessPiece.WhiteRook, false, it)
        in 8..15 -> BoardSquareState(ChessPiece.WhitePawn, false, it)

        in 48..55 -> BoardSquareState(ChessPiece.BlackPawn, false, it)
        56 -> BoardSquareState(ChessPiece.BlackRook, false, it)
        57 -> BoardSquareState(ChessPiece.BlackKnight, false, it)
        58 -> BoardSquareState(ChessPiece.BlackBishop, false, it)
        59 -> BoardSquareState(ChessPiece.BlackQueen, false, it)
        60 -> BoardSquareState(ChessPiece.BlackKing, false, it)
        61 -> BoardSquareState(ChessPiece.BlackBishop, false, it)
        62 -> BoardSquareState(ChessPiece.BlackKnight, false, it)
        63 -> BoardSquareState(ChessPiece.BlackRook, false, it)

        else -> BoardSquareState(null, false, it)
    }
}

fun Piece.toBoardSquareState(
    isSelectedForMove: Boolean,
    index: Int
): BoardSquareState {

    val piece =  when(this){
        Piece.WHITE_PAWN -> ChessPiece.WhitePawn
        Piece.WHITE_KNIGHT -> ChessPiece.WhiteKnight
        Piece.WHITE_BISHOP ->ChessPiece.WhiteBishop
        Piece.WHITE_ROOK -> ChessPiece.WhiteRook
        Piece.WHITE_QUEEN -> ChessPiece.WhiteQueen
        Piece.WHITE_KING -> ChessPiece.WhiteKing
        Piece.BLACK_PAWN -> ChessPiece.BlackPawn
        Piece.BLACK_KNIGHT -> ChessPiece.BlackKnight
        Piece.BLACK_BISHOP -> ChessPiece.BlackBishop
        Piece.BLACK_ROOK -> ChessPiece.BlackRook
        Piece.BLACK_QUEEN -> ChessPiece.BlackQueen
        Piece.BLACK_KING -> ChessPiece.BlackKing
        Piece.NONE -> null
    }

    return BoardSquareState(piece, isSelectedForMove, index)
}