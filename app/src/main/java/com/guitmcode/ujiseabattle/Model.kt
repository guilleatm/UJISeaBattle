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
		PLAYER_TURN,
		COMPUTER_TURN,
		END
	}


	fun createComputerBoard(ships: Array<Ship>) {
		for (ship in ships) {
			ship.set = true

			do {
				ship.coordsTablero = Pair(Random.nextInt(0, computerBoard.numCells - ship.occupedCells), Random.nextInt(0, computerBoard.numCells - ship.occupedCells))
			} while (!ship.fits(ship.coordsTablero!!.first, ship.coordsTablero!!.second, computerBoard))
			ship.setCoords() // Para meter las coordenadas reales para e dibujado de sprites
		}
		computerBoard.setShipsOnBoard(ships)
	}


	fun bomb(col: Int, row: Int) {

		if (state == SeaBattleAction.PLAYER_TURN) {

			val touched = isTouched(computerBoard, col, row)
			val isBombed = computerBoard.isBombed(col, row)

			if (isBombed) return

			computerBoard.bombedCells += Triple(col, row, touched)

			if (touched) {
				val touchedShip = computerBoard.getShip(col, row)
				touchedShip!!.updateSank()
				val playerWins = computerBoard.allShipsSank()

				if (playerWins) {
					state = SeaBattleAction.END
					Log.d("marselo", "Todos los barcos del computer hundidos")
				}
			} else {
				state = SeaBattleAction.COMPUTER_TURN
				computerBomb()
			}

		} else if (state == SeaBattleAction.COMPUTER_TURN) {

			val touched = isTouched(playerBoard, col, row)
			val isBombed = playerBoard.isBombed(col, row)

			if (isBombed) computerBomb()

			playerBoard.bombedCells += Triple(col, row, touched)

			if (touched) {

				val touchedShip = playerBoard.getShip(col, row)
				touchedShip!!.updateSank()

				val computerWins = playerBoard.allShipsSank()

				if (computerWins) {
					state = SeaBattleAction.END
					Log.d("marselo", "Computer wins")
				}
				else {
					computerBomb()
				}
			} else {
				state = SeaBattleAction.PLAYER_TURN
			}

		}
		return
	}

	fun isTouched(board: Board, col: Int, row: Int) = board.cells[row][col] == Board.CellState.SHIP

	fun updateGameState() {

		if (state == SeaBattleAction.PLACE_SHIPS) {
			for (ship in ships) {
				if (!ship.set) return
			}
			playerBoard.setShipsOnBoard(ships) // #JIC
			state = SeaBattleAction.PLAYER_TURN
		}
	}

	private fun computerBomb () {
		bomb(Random.nextInt(0, playerBoard.numCells), Random.nextInt(0, playerBoard.numCells))
	}
}