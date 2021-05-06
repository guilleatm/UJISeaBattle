package com.guitmcode.ujiseabattle

import android.util.Log
import java.util.*
import kotlin.random.Random

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

	fun createComputerBoard(ships: Array<Ship>) {
		for (ship in ships) {
			ship.set = true
			ship.coordsTablero = Pair(Random.nextInt(0, computerBoard.numCells - ship.occupedCells), Random.nextInt(0, computerBoard.numCells - ship.occupedCells))
		}
		computerBoard.setShipsOnBoard(ships)
	}

	fun restart() {
	}


	fun bomb(col: Int, row: Int) {

		if (state == SeaBattleAction.PLAYER_TURN) {

			val touched = isTouched(computerBoard, col, row)
			val isBombed = computerBoard.isBombed(col, row)

			if (isBombed) return

			computerBoard.bombedCells += Triple(col, row, touched)

			if (touched) {
				Log.d("marselo", "cagaste crack")
			}
			else {
				state = SeaBattleAction.COMPUTER_TURN

				computerBomb()
			}

		} else if (state == SeaBattleAction.COMPUTER_TURN) {

			val touched = isTouched(playerBoard, col, row)
			val isBombed = playerBoard.isBombed(col, row)

			if (isBombed) return

			playerBoard.bombedCells += Triple(col, row, touched)

			if (touched) {
				Log.d("marselo", "cagaste fiera")
				computerBomb()
			}
			else {
				state = SeaBattleAction.PLAYER_TURN
			}

		}
		return
	}

	fun isTouched(board: Board, col: Int, row: Int) = board.cells[row][col] == Board.CellState.SHIP

	fun updateGameState() {

		if (state == SeaBattleAction.PLACE_SHIPS) {
			for (ship in ships) {
				if (ship.set == false) return
			}
			playerBoard.setShipsOnBoard(ships)
			state = SeaBattleAction.PLAYER_TURN
		}
	}

	fun computerBomb () {
		bomb(Random.nextInt(0, playerBoard.numCells), Random.nextInt(0, playerBoard.numCells))
	}
}