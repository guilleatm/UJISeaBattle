package com.guitmcode.ujiseabattle

import android.content.pm.ActivityInfo
import android.util.DisplayMetrics
import com.guitmcode.ujiseabattle.Controller
import es.uji.vj1229.framework.GameActivity
import es.uji.vj1229.framework.IGameController

class MainActivity : GameActivity() {
	private lateinit var controller: Controller
	override fun buildGameController(): IGameController {

		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

		val sound = if (intent.getBooleanExtra("SOUND", true)) 1f else 0f


		val displayMetrics = DisplayMetrics()
		@Suppress("DEPRECATION")
		windowManager.defaultDisplay.getMetrics(displayMetrics)

		controller = Controller(displayMetrics.widthPixels, displayMetrics.heightPixels, applicationContext, sound)
		return controller
	}
}