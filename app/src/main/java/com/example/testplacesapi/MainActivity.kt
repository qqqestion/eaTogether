package com.example.testplacesapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapButton = findViewById<Button>(R.id.map_button)
        val intent = Intent(this, MapsActivity::class.java)
        mapButton.setOnClickListener(View.OnClickListener {
            startActivity(intent)
        })
    }
}