package ru.blackbull.eatogether

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

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