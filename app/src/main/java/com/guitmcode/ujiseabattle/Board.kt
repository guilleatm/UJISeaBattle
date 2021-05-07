package com.guitmcode.ujiseabattle

class Board(val numCells: Int, val oI: Pair<Int, Int>, cellSize: Float) {

	var cells: Array<Array<CellState>>
	val origin: Pair<Float, Float> = Pair(oI.first * cellSize, oI.second * cellSize)

	var bombedCells: List<Triple<Int, Int, Boolean>> = listOf()
	var ships: Array<Ship>? = null

	enum class CellState {
		WATER, SHIP
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
		return col in oI.first until oI.first + numCells && row in oI.second until oI.second + numCells
	}

	fun setShipOnBoard(ship: Ship) {
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

	fun setShipsOnBoard(ships : Array<Ship>) {
		this.ships = ships
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

	fun isBombed(col: Int, row: Int): Boolean {

		for (bombedCell in bombedCells)
			if (bombedCell.first == col && bombedCell.second == row)
				return true
		return false
	}

	fun getShip (col: Int, row: Int): Ship? {
		for (ship in ships!!) {
			if (ship.inCell(col, row)) return ship
		}
		return null
	}

	fun allShipsSank(): Boolean {
		for (ship in ships!!)
			if (!ship.isSank()) return false
		return true
	}

	fun isTouched(col: Int, row: Int) = cells[row][col] == CellState.SHIP
}