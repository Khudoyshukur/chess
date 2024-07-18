package uz.safix.chess.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import uz.safix.chess.R

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:53 AM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Stable
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
val ChessPiece.getDrawable
    get() = when (this) {
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
    when (it) {
        0 -> BoardSquareState(ChessPiece.WhiteRook, it)
        1 -> BoardSquareState(ChessPiece.WhiteKnight, it)
        2 -> BoardSquareState(ChessPiece.WhiteBishop, it)
        3 -> BoardSquareState(ChessPiece.WhiteQueen, it)
        4 -> BoardSquareState(ChessPiece.WhiteKing, it)
        5 -> BoardSquareState(ChessPiece.WhiteBishop, it)
        6 -> BoardSquareState(ChessPiece.WhiteKnight, it)
        7 -> BoardSquareState(ChessPiece.WhiteRook, it)
        in 8..15 -> BoardSquareState(ChessPiece.WhitePawn, it)

        in 48..55 -> BoardSquareState(ChessPiece.BlackPawn, it)
        56 -> BoardSquareState(ChessPiece.BlackRook, it)
        57 -> BoardSquareState(ChessPiece.BlackKnight, it)
        58 -> BoardSquareState(ChessPiece.BlackBishop, it)
        59 -> BoardSquareState(ChessPiece.BlackQueen, it)
        60 -> BoardSquareState(ChessPiece.BlackKing, it)
        61 -> BoardSquareState(ChessPiece.BlackBishop, it)
        62 -> BoardSquareState(ChessPiece.BlackKnight, it)
        63 -> BoardSquareState(ChessPiece.BlackRook, it)

        else -> BoardSquareState(null, it)
    }
}

fun Piece.toBoardSquareState(index: Int): BoardSquareState {

    val piece = when (this) {
        Piece.WHITE_PAWN -> ChessPiece.WhitePawn
        Piece.WHITE_KNIGHT -> ChessPiece.WhiteKnight
        Piece.WHITE_BISHOP -> ChessPiece.WhiteBishop
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

    return BoardSquareState(piece, index)
}

fun ChessPiece?.toPiece() = when (this) {
    ChessPiece.WhiteKing -> Piece.WHITE_KING
    ChessPiece.WhiteQueen -> Piece.WHITE_QUEEN
    ChessPiece.WhiteRook -> Piece.WHITE_ROOK
    ChessPiece.WhiteBishop -> Piece.WHITE_BISHOP
    ChessPiece.WhiteKnight -> Piece.WHITE_KNIGHT
    ChessPiece.WhitePawn -> Piece.WHITE_PAWN
    ChessPiece.BlackKing -> Piece.BLACK_KING
    ChessPiece.BlackQueen -> Piece.BLACK_QUEEN
    ChessPiece.BlackRook -> Piece.BLACK_ROOK
    ChessPiece.BlackBishop -> Piece.BLACK_BISHOP
    ChessPiece.BlackKnight -> Piece.BLACK_KNIGHT
    ChessPiece.BlackPawn -> Piece.BLACK_PAWN
    null -> Piece.NONE
}