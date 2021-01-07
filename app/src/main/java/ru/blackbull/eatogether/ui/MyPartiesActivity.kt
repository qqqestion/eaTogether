package ru.blackbull.eatogether.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.repository.FirebaseRepository
import ru.blackbull.eatogether.ui.fragments.MyPartyFragment
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel

class MyPartiesActivity : AppCompatActivity() {
    val userPartiesViewModel: FirebaseViewModel by viewModels()
    val placeViewModel: PlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_parties)
        val fragment = MyPartyFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.flFragment,fragment)
            .commit()
    }
}