package es.uji.jvilar.frameworktest

import android.content.pm.ActivityInfo
import android.util.DisplayMetrics
import android.view.View
import es.uji.vj1229.framework.GameActivity
import es.uji.vj1229.framework.IGameController

class MainActivity : GameActivity() {
    private lateinit var controller: Controller
    override fun buildGameController(): IGameController {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        controller = Controller(displayMetrics.widthPixels, displayMetrics.heightPixels, applicationContext)
        return controller
    }
}