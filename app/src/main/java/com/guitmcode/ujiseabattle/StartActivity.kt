package com.guitmcode.ujiseabattle

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch

class StartActivity : AppCompatActivity() {
	lateinit var switch: Switch

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)
		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

		switch = this.findViewById<Switch>(R.id.switch1)
	}

	fun onPlayClick(view: View) {
		val intent = Intent(this, MainActivity::class.java).apply {
			putExtra("SOUND", switch.isChecked)
		}
		startActivity(intent)

	}
}