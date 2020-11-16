package com.example.testplacesapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        val fragment = RecycleRestaurantsFragment.newInstance(
            intent.getDoubleExtra("lat", 0.0),
            intent.getDoubleExtra("lng", 0.0)
        )
        supportFragmentManager.beginTransaction().add(
            R.id.layout_for_fragments,
            fragment
        ).commit()
    }
}