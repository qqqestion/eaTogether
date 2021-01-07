package ru.blackbull.eatogether.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.fragments.CardFragment
import ru.blackbull.eatogether.ui.viewmodels.NearbyViewModel

class NearbyActivity : AppCompatActivity() , AdapterView.OnItemClickListener {
    private lateinit var titles: Array<String>
    lateinit var drawerList: ListView
    lateinit var drawerLayout: DrawerLayout

    val nearbyViewModel: NearbyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)
        titles = resources.getStringArray(R.array.titles)
        drawerList = findViewById(R.id.drawer)
        drawerList.adapter = ArrayAdapter<String>(
            this ,
            android.R.layout.simple_list_item_1 ,
            titles
        )
        drawerList.onItemClickListener = this

        drawerLayout = findViewById(R.id.drawer_layout)
        val fragment: Fragment =
            CardFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.content_frame , fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.commit()
    }

    override fun onItemClick(adapter: AdapterView<*>? , view: View? , i: Int , l: Long) {
        val intent = when (titles[i]) {
            "Карта" -> Intent(this , MapsActivity::class.java)
            "Профиль" -> Intent(this , ProfileActivity::class.java)
            "Мои мероприятия" -> Intent(this , MyPartiesActivity::class.java)
            else -> return
        }
        startActivity(intent)
        drawerLayout.closeDrawer(drawerList)
    }
}