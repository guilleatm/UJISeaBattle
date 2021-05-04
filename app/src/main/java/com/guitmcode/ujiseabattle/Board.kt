package com.guitmcode.ujiseabattle

class Board(val numCells: Int, val oI: Pair<Int, Int>, cellSize: Float) {

	var cells: Array<Array<CellState>>
	val origin: Pair<Float, Float> = Pair(oI.first * cellSize, oI.second * cellSize)

	var bombedCells: List<Pair<Int, Int>> = listOf()

	enum class CellState {
		WATER, SHIP, BOMBED
	}

	init {
		// Init table shots
		cells = arrayOf<Array<CellState>>()

		for (i in 0 .. numCells) {
			var col = arrayOf<CellState>()
			for (j in 0 .. numCells) {
				col += CellState.WATER
			}
			cells += col
		}
	}

	fun inBoard(col: Int, row: Int): Boolean {
		return col in oI.first .. (oI.first + numCells) && row in oI.second .. (oI.second + numCells)
	}

	fun setShipsOnBoard(ships : Array<Ship>) {
		for (ship in ships) {
			if (ship.isHorizontal) {
				for (i in 0 until ship.occupedCells) {
					this.cells[ship.coordsTablero!!.second][ship.coordsTablero!!.first + i] = CellState.SHIP
				}
			} else {
				for (i in 0 until ship.occupedCells) {
					this.cells[ship.coordsTablero!!.second + i][ship.coordsTablero!!.first] = CellState.SHIP
				}
			}
		}
	}
}