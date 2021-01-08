package ru.blackbull.eatogether.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_temp.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.ui.fragments.PlaceDetailFragment
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
        intent.extras?.let { bundle ->
            placeId = bundle.getString(PLACE_ID).toString()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.temp_layout_for_fragments , PlaceDetailFragment.newInstance(placeId))
            .commit()
    }
}