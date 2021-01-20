package ru.blackbull.eatogether.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.map.RecycleRestaurantsFragment
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel

class InformationActivity : AppCompatActivity() {
    val placeViewModel: PlaceViewModel by viewModels()
    val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
//        val fragment = RecycleRestaurantsFragment.newInstance(
//            intent.getDoubleExtra("lat" , 0.0) ,
//            intent.getDoubleExtra("lng" , 0.0)
//        )
//        supportFragmentManager.beginTransaction().add(
//            R.id.layout_for_fragments ,
//            fragment
//        ).commit()
    }
}