package com.guitmcode.ujiseabattle

class Board(val numCells: Int, oI: Pair<Int, Int>, cellSize: Float) {

	var cells: Array<Array<CellState>>
	val origin: Pair<Float, Float> = Pair(oI.first * cellSize, oI.second * cellSize)

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
}