package ru.blackbull.eatogether.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.fragments.EditProfileFragment
import ru.blackbull.eatogether.ui.fragments.ProfileFragment
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel

class ProfileActivity : AppCompatActivity() , View.OnClickListener ,
    OnItemClickListener {

    private lateinit var titles: Array<String>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerList: ListView
    private lateinit var mAuth: FirebaseAuth

    val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mAuth = FirebaseAuth.getInstance()
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword("hello.world@mail.ru" , "Aa123456789")
            .addOnFailureListener { e ->
                Log.d("XiaomiDebug" , "auth error occurred" , e)
                Toast.makeText(
                    this ,
                    "Ошибка авторизации" ,
                    Toast.LENGTH_SHORT
                ).show()
            }

        titles = resources.getStringArray(R.array.titles)
        drawerLayout = findViewById(R.id.profile_drawer_layout)
        drawerList = findViewById(R.id.profile_drawer)
        drawerList.adapter = ArrayAdapter(
            this ,
            android.R.layout.simple_list_item_1 ,
            titles
        )
        drawerList.onItemClickListener = this
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.profile_fragment_container , ProfileFragment())
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.commit()
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if (user == null) {
            val intent = Intent(application , StartActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.profile_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_container , EditProfileFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit()
            }
            R.id.profile_menu_btn -> drawerLayout.openDrawer(GravityCompat.START)
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList)
        } else {
            super.onBackPressed()
        }
    }

    override fun onItemClick(
        adapterView: AdapterView<*>? ,
        view: View ,
        i: Int ,
        l: Long
    ) {
        if ("Карта" == titles[i]) {
            val intent = Intent(this , MapsActivity::class.java)
            startActivity(intent)
        }
        drawerLayout.closeDrawer(drawerList)
    }
}