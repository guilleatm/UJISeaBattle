package com.guitmcode.ujiseabattle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class StartActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)
	}

	fun onPlayClick(view: View) {
		val intent = Intent(this, MainActivity::class.java).apply {
			putExtra("SOUND", "off")
		}
		startActivity(intent)

	}
}