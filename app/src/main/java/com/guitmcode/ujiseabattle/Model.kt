package com.guitmcode.ujiseabattle

import android.util.Log

class Model(private val soundPlayer: SoundPlayer, val playerBoard: Board, val computerBoard: Board, val ships: Array<Ship>) {
	interface SoundPlayer {
		fun playVictory()
		fun playMove()
	}

	var state = SeaBattleAction.PLACE_SHIPS
		private set

	enum class SeaBattleAction {
		PLACE_SHIPS,
		DRAG_INTO_BOARD,
		DRAG_INSIDE_BOARD,
		PLAYER_TURN,
		COMPUTER_TURN,
		WAITING,
		END
	}

	init {
		restart()
	}

	fun restart() {
	}



	fun onBoardClick(col: Int, row: Int) {

		if (state == SeaBattleAction.PLAYER_TURN) {
			bomb(col, row)
		} else if (state == SeaBattleAction.PLACE_SHIPS) {

		}
	}

	fun bomb(col: Int, row: Int): Boolean {

		if (state == SeaBattleAction.PLAYER_TURN) {
			computerBoard.bombedCells += Pair(col, row)

			val touched = isTouched(computerBoard, col, row)


		} else if (state == SeaBattleAction.COMPUTER_TURN) {

			playerBoard.bombedCells += Pair(col, row)

			val touched = isTouched(playerBoard, col, row)

		}
		return false
	}

	fun isTouched(board: Board, col: Int, row: Int) = board.cells[row][col] == Board.CellState.SHIP

	fun updateGameState() {

		if (state == SeaBattleAction.PLACE_SHIPS) {
			for (ship in ships) {
				if (ship.set == false) return
			}
			state = SeaBattleAction.PLAYER_TURN
		}
	}

}