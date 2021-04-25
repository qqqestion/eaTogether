package ru.blackbull.eatogether.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.Match
import ru.blackbull.eatogether.other.Constants.START_SERVICE
import ru.blackbull.eatogether.other.Constants.STOP_SERVICE
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.services.MainService
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("3f863532-8f11-409b-9f01-410fec3a2c9b")
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
//
//        Intent(this , MapActivity::class.java).also {
//            startActivity(it)
//            finish()
//        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profileFragment , R.id.mapFragment , R.id.nearbyFragment , R.id.myPartyFragment
            ) ,
            rootLayout
        )
        setupActionBarWithNavController(navController , appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            // Фрагмент кода, который препятствует повторной загрузке
            // фрагмента при его перевыборе в меню
            val itId = it.itemId
            val curId = navController.currentDestination?.id
            if (itId != curId) {
                navController.navigate(
                    itId
                )
            }
            rootLayout.closeDrawer(GravityCompat.START)
            itId != curId
        }
//        Intent(this , MainService::class.java).also { intent ->
//            intent.action = START_SERVICE
//            this.startService(intent)
//        }
    }

    override fun onBackPressed() {
        if (rootLayout.isDrawerOpen(GravityCompat.START)) {
            rootLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}