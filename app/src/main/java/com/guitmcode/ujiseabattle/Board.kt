package com.guitmcode.ujiseabattle

class Board(val numCells: Int, val origin: Pair<Int, Int>, cellSize: Float) {

	var cells: Array<Array<Boolean>>


	init {
		// Init table shots
		cells = arrayOf<Array<Boolean>>()

		for (i in 0 .. numCells) {
			var col = arrayOf<Boolean>()
			for (j in 0 .. numCells) {
				col += false
			}
			cells += col
		}
	}
}