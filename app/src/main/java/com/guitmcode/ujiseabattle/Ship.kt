package com.guitmcode.ujiseabattle

import android.graphics.drawable.Drawable
import es.uji.vj1229.framework.TouchHandler

class Ship (val drawable: Drawable, val occupedCells: Int, val cellSide: Int, val index: Int, val isHorizontal: Boolean = true){

	var coords: Pair<Int, Int> = Pair(0, 0)
	var set = false


	fun clicked(event: TouchHandler.TouchEvent): Boolean {
		return (isHorizontal && event.x in coords.first .. (coords.first + occupedCells * cellSide) && event.y in coords.second .. (coords.second + cellSide)) ||
				(!isHorizontal && event.x in coords.first .. (coords.first + cellSide) && event.y in coords.second .. (coords.second + occupedCells * cellSide))
	}

}