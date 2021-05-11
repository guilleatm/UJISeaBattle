package com.guitmcode.ujiseabattle

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import es.uji.vj1229.framework.TouchHandler

private const val DEFAULT_COL = 14
private const val DEFAULT_ROW = 2

class Ship (val drawable: Drawable, val occupedCells: Int, val cellSide: Int, val index: Int, var isHorizontal: Boolean = true){

	var coords: Pair<Int, Int> = Pair((DEFAULT_COL + index - 2) * cellSide, (DEFAULT_ROW + (2 * occupedCells)) * cellSide)
	var coordsTablero: Pair<Int, Int>? = null
	val OCoords = coords
	var set = false
	var sank = occupedCells


	fun clicked(event: TouchHandler.TouchEvent): Boolean {
		return (isHorizontal && event.x in coords.first .. (coords.first + occupedCells * cellSide) && event.y in coords.second .. (coords.second + cellSide)) ||
				(!isHorizontal && event.x in coords.first .. (coords.first + cellSide) && event.y in coords.second .. (coords.second + occupedCells * cellSide))
	}

	fun fits(col: Int, row: Int, board: Board): Boolean {
		if ((col >= 9 && isHorizontal) || (row >= 9 && !isHorizontal)) {
			return false
		}
		if (isHorizontal) {
			for (i in 0 until occupedCells) {
				if (board.cells[row][col + i] == Board.CellState.SHIP) {
					return false
				}
			}
		} else {
			for (i in 0 until occupedCells) {
				if (board.cells[row + i][col] == Board.CellState.SHIP) {
					return false
				}
			}
		}

		return (isHorizontal && col + occupedCells <= board.numCells) || (!isHorizontal && row + occupedCells <= board.numCells)
	}

	fun inCell(col: Int, row: Int): Boolean {
		val cell = Pair(col, row)
		if (isHorizontal) {
			for (i in 0 until occupedCells) {
				if (cell == Pair(coordsTablero!!.first + i, coordsTablero!!.second)) {
					return true
				}
			}
		}
		else {
			for (j in 0 until occupedCells) {
				if (cell == Pair(coordsTablero!!.first, coordsTablero!!.second + j)) {
					return true
				}
			}
		}
		return false
	}

	fun updateSank() {
		sank--
	}

	fun isSank() = sank <= 0

	fun setCoords() {
		coords = Pair((DEFAULT_COL + coordsTablero!!.first) * cellSide, (DEFAULT_ROW + coordsTablero!!.second) * cellSide)
	}

}