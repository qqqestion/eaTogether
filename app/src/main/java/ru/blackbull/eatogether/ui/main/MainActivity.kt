package ru.blackbull.eatogether.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import ru.blackbull.eatogether.R


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Yandex MapKit init
         * Setup api key before initialization
         */
        MapKitFactory.setApiKey("3f863532-8f11-409b-9f01-410fec3a2c9b")
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.mainNavFragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profileFragment , R.id.mapFragment , R.id.nearbyFragment , R.id.myPartyFragment
            ) ,
            rootLayout
        )
        setupActionBarWithNavController(navController , appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            /**
             * Фрагмент кода, который препятствует повторной загрузке
             * фрагмента при его перевыборе в меню
             */
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

        /**
         * Start service
         */
//        Intent(this , MainService::class.java).also { intent ->
//            intent.action = START_SERVICE
//            this.startService(intent)
//        }
    }

    /**
     * If drawer menu open, close it, if not, super.onBackPressed()
     */
    override fun onBackPressed() {
        if (rootLayout.isDrawerOpen(GravityCompat.START)) {
            rootLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mainNavFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}