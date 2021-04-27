package com.example.ujiseabattle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton

class MainActivity : AppCompatActivity() {

    lateinit var boton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        boton = findViewById(R.id.button)
        Log.d("marselo", "yeyeyeyeye")

        boton.setOnClickListener {
            Log.d("marselo", "yeyeyeyeye1")
            val intent = Intent(this, UJIBattleActivity::class.java)
            Log.d("marselo", "yeyeyeyeye2")
            startActivity(intent)
            Log.d("marselo", "yeyeyeyeye3")
        }
    }

    /*fun startGame(view: View) {
    }*/
}