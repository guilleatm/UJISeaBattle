package com.example.ujiseabattle

import android.graphics.Bitmap
import es.uji.vj1229.framework.IGameController
import es.uji.vj1229.framework.TouchHandler

class UJISeaController : IGameController {

    val width : Int
    val height : Int

    constructor(width : Int, height : Int) {
        this.width = width
        this.height = height
    }

    override fun onUpdate(deltaTime: Float, touchEvents: MutableList<TouchHandler.TouchEvent>?) {
        TODO("Not yet implemented")
    }

    override fun onDrawingRequested(): Bitmap {
        TODO("Not yet implemented")
    }
}