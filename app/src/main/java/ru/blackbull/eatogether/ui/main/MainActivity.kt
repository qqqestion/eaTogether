package ru.blackbull.eatogether.ui.main

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import ru.blackbull.eatogether.R
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profileFragment , R.id.mapFragment , R.id.cardFragment , R.id.myPartyFragment
            ) ,
            rootLayout
        )
        setupActionBarWithNavController(navController , appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}