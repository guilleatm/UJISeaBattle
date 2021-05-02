package com.example.ujiseabattle

import android.util.DisplayMetrics
import es.uji.vj1229.framework.GameActivity
import es.uji.vj1229.framework.IGameController

class UJIBattleActivity : GameActivity() {
    override fun buildGameController(): IGameController {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return UJISeaController(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}