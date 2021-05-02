package com.guitmcode.ujiseabattle

import android.graphics.drawable.Drawable
import es.uji.vj1229.framework.TouchHandler

class Ship (val drawable: Drawable){

	var coords: Pair<Int, Int> = Pair(0, 0)
	init {

	}

	fun clicked(event: TouchHandler.TouchEvent): Boolean {
		return true
	}

}