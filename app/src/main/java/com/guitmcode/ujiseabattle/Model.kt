package com.guitmcode.ujiseabattle

import android.util.Log

class Model(private val soundPlayer: SoundPlayer, val playerBoard: Board, val computerBoard: Board) {
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

	fun bomb(col: Int, row: Int) {

		if (state == SeaBattleAction.PLAYER_TURN && computerBoard.cells[row][col] == Board.CellState.SHIP) {
			Log.d("gbug", "Jugador toca!!")
		} else if (state == SeaBattleAction.COMPUTER_TURN && playerBoard.cells[row][col] == Board.CellState.SHIP) {
			Log.d("gbug", "Máquina toca!!")
		}
	}

}