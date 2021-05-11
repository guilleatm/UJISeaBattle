package com.guitmcode.ujiseabattle

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class StartActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)
		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
	}

	fun onPlayClick(view: View) {
		val intent = Intent(this, MainActivity::class.java).apply {
			putExtra("SOUND", "off")
		}
		startActivity(intent)

	}
}