package uz.safix.engine_stockfish

internal interface StockfishJni {
    fun init()
    fun main(threadCount: Int, skillLevel: Int)
    fun readLine(): String?
    fun writeLine(cmd: String)
}

internal class AndroidStockfishJni : StockfishJni {

    override fun init() {
        System.loadLibrary("stockfish")
    }

    external override fun main(threadCount: Int, skillLevel: Int)
    external override fun readLine(): String

    private external fun write(command: String): Boolean

    override fun writeLine(cmd: String) {
        write("$cmd\n")
    }
}
