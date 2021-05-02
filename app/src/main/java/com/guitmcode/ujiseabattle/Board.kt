package com.guitmcode.ujiseabattle

class Board(val numCells: Int, oI: Pair<Int, Int>, cellSize: Float) {

	var cells: Array<Array<Boolean>>
	val origin: Pair<Float, Float> = Pair(oI.first * cellSize, oI.second * cellSize)


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