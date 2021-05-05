package com.guitmcode.ujiseabattle

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import es.uji.vj1229.framework.TouchHandler

private const val DEFAULT_COL = 14
private const val DEFAULT_ROW = 2

class Ship (val drawable: Drawable, val occupedCells: Int, val cellSide: Int, val index: Int, val isHorizontal: Boolean = true){

	var coords: Pair<Int, Int> = Pair(DEFAULT_COL * cellSide, (DEFAULT_ROW + (index * 2)) * cellSide)
	var coordsTablero: Pair<Int, Int>? = null
	val OCoords = coords
	var set = false


	fun clicked(event: TouchHandler.TouchEvent): Boolean {
		return (isHorizontal && event.x in coords.first .. (coords.first + occupedCells * cellSide) && event.y in coords.second .. (coords.second + cellSide)) ||
				(!isHorizontal && event.x in coords.first .. (coords.first + cellSide) && event.y in coords.second .. (coords.second + occupedCells * cellSide))
	}

	fun fits(col: Int, row: Int, board: Board): Boolean {
		return (isHorizontal && col + occupedCells <= board.numCells) || (!isHorizontal && row + occupedCells <= board.numCells)
	}

}