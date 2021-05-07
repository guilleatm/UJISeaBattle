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
	var lastTouch: Pair<Int, Int>? = null

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

			val touched = computerBoard.isTouched(col, row)
			val isBombed = computerBoard.isBombed(col, row)

			if (isBombed) return

			Log.d("marselo", Pair(col,row).toString())

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

			val touched = playerBoard.isTouched(col, row)
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
		var targetCol: Int? = null
		var targetRow: Int? = null

		if (lastTouch == null) {
			targetCol = Random.nextInt(0, playerBoard.numCells)
			targetRow = Random.nextInt(0, playerBoard.numCells)
		} else {
			val (col, row) = lastTouch!!
			if (col + 1 < playerBoard.numCells && !playerBoard.isBombed(col + 1, row)) { // Derecha
				targetCol = col + 1
				targetRow = row
			}
			else if (col - 1 > 0 && !playerBoard.isBombed(col - 1, row)) { // Izquierda
				targetCol = col - 1
				targetRow = row
			}
			else if (row - 1 > 0 && !playerBoard.isBombed(col, row - 1)) { // Arriba
				targetCol = col
				targetRow = row - 1
			}
			else if (row + 1 < playerBoard.numCells && !playerBoard.isBombed(col, row + 1)) { // Abajo
				targetCol = col
				targetRow = row + 1
			} else {
				targetCol = Random.nextInt(0, playerBoard.numCells)
				targetRow = Random.nextInt(0, playerBoard.numCells)
			}
		}

		val touched = playerBoard.isTouched(targetCol, targetRow)
		bomb(targetCol, targetRow)

		if (touched) {
			lastTouch = Pair(targetCol, targetRow)
			val touchedShip = playerBoard.getShip(targetCol, targetRow)

			if (touchedShip!!.isSank()) {
				lastTouch = null
			}
		}
	}
}