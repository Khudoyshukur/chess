package uz.safix.engine_lc0

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 3:36 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

sealed interface EngineCommand {

    data class SetPosition(private val fen: String) : EngineCommand {
        override fun toString() = "position fen $fen"
    }

    data class GoDepth(private val depth: Int) : EngineCommand {
        override fun toString() = "go depth $depth"
    }

    object GoNodes : EngineCommand {
        override fun toString() = "go nodes 1"
    }

    object Quit : EngineCommand {
        override fun toString() = "quit"
    }
}