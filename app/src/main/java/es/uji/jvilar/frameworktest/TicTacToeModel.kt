package es.uji.jvilar.frameworktest

class TicTacToeModel(private val soundPlayer: SoundPlayer) {
    interface SoundPlayer {
        fun playVictory()
        fun playMove()
    }

    enum class SquareColor {
        EMPTY, RED, BLUE
    }

    private val board: Array<Array<SquareColor>> = Array(3) { Array(3) {SquareColor.EMPTY} }
    var turn: SquareColor = SquareColor.EMPTY
        private set
    var winner: SquareColor = SquareColor.EMPTY
        private set
    val winnerCells: Array<IntArray> = Array(3) { intArrayOf(0, 0) }
    private var turnCount = 0

    init {
        restart()
    }

    fun restart() {
        turnCount = 0
        for (row in 0..2)
            for (column in 0..2)
                board[row][column] = SquareColor.EMPTY
        turn = SquareColor.RED
        winner = SquareColor.EMPTY
    }

    fun getSquare(row: Int, column: Int): SquareColor {
        return board[row][column]
    }

    fun canPlay(row: Int, column: Int): Boolean {
        return row in 0 .. 2 && column in 0 .. 2 &&
                turn != SquareColor.EMPTY &&
                board[row][column] == SquareColor.EMPTY
    }

    fun play(row: Int, column: Int) {
        board[row][column] = turn
        soundPlayer.playMove()
        val wins = winsRow(row) || winsColumn(column) || winsDiagonal(row, column)
        if (wins) {
            winner = turn
            turn = SquareColor.EMPTY
            soundPlayer.playVictory()
        } else {
            turnCount++
            if (turnCount == 9) {
                winner = SquareColor.EMPTY
                turn = SquareColor.EMPTY
            } else turn = if (turn == SquareColor.RED) SquareColor.BLUE else SquareColor.RED
        }
    }

    private fun fillWinnerCells(wc00: Int, wc01: Int, wc10: Int, wc11: Int, wc20: Int, wc21: Int) {
        winnerCells[0][0] = wc00
        winnerCells[0][1] = wc01
        winnerCells[1][0] = wc10
        winnerCells[1][1] = wc11
        winnerCells[2][0] = wc20
        winnerCells[2][1] = wc21
    }

    private fun winsRow(row: Int): Boolean {
        if (board[row][0] != board[row][1] || board[row][1] != board[row][2])
            return false
        fillWinnerCells(row, 0, row, 1, row, 2)
        return true
    }

    private fun winsColumn(column: Int): Boolean {
        if (board[0][column] != board[1][column] || board[1][column] != board[2][column])
            return false
        fillWinnerCells(0, column,1, column, 2, column)
        return true
    }

    private fun winsDiagonal(row: Int, column: Int): Boolean {
        if (row == column && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            fillWinnerCells(0, 0, 1, 1, 2, 2)
            return true
        }
        if (row == 2 - column && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            fillWinnerCells(0, 2, 1, 1, 2, 0)
            return true
        }
        return false
    }

}