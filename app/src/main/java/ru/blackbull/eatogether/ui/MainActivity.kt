package ru.blackbull.eatogether.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
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
import ru.blackbull.eatogether.BuildConfig
import ru.blackbull.eatogether.R


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Сначала ставим api ключ, потом инициализируем MapKit
         * Если активити пересоздается из-за изменения конфигураций,
         * то повторно инициализировать не надо (приложение падает :/)
         */
        if (savedInstanceState == null) {
            try {
                MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_API_KEY)
                MapKitFactory.initialize(this)
                SearchFactory.initialize(this)
            } catch (exc: AssertionError) {
                /**
                 * При повторном запуске активити, приложение тоже падает :/
                 * Например, при создании сначала MainActivity,
                 * потом выходе в AuthActivity, и повторном создании MainActivity
                 */
            }
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.mainNavFragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profileFragment ,
                R.id.friendsFragment ,
                R.id.mapFragment ,
                R.id.nearbyFragment ,
                R.id.myPartyFragment ,
                R.id.lunchInvitationsFragment
            ) ,
            rootLayout
        )
        /**
         * Убираем клавиатуру при открытии бокового меню
         */
        val drawerListener = object : ActionBarDrawerToggle(
            this ,
            rootLayout ,
            R.string.drawer_open ,
            R.string.drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val imm =
                    this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(rootLayout.windowToken , 0)
            }
        }
        rootLayout.addDrawerListener(drawerListener)

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
         * Запускаем сервис
         */
//        Intent(this , MainService::class.java).also { intent ->
//            intent.action = START_SERVICE
//            this.startService(intent)
//        }
    }

    /**
     * Если боковое меню открыто, то закрываем его
     * Если не закрыто, то по стандарту super.onBackPressed()
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