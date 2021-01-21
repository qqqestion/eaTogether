package ru.blackbull.eatogether.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.map.PlaceDetailFragment
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel

private const val PLACE_ID: String = "placeId"

class TempActivity : AppCompatActivity() {
    private lateinit var placeId: String
    val placeViewModel: PlaceViewModel by viewModels()
    val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)
    }
}