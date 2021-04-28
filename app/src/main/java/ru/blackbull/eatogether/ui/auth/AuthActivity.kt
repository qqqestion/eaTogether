package ru.blackbull.eatogether.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setSupportActionBar(toolbar)
        Timber.d("Hello from AuthActivity")

        if (FirebaseAuth.getInstance().currentUser != null) {
            Intent(this , MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}