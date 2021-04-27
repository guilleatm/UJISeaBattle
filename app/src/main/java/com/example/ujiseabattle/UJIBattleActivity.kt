package com.example.ujiseabattle

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import es.uji.vj1229.framework.GameActivity
import es.uji.vj1229.framework.IGameController

class UJIBattleActivity : GameActivity() {
    override fun buildGameController(): IGameController {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return UJISeaController(displayMetrics.widthPixels,
            displayMetrics.heightPixels, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_battle)
        Log.d("marselo", "delocos")
    }
}