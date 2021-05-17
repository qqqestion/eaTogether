package ru.blackbull.eatogether.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        Timber.d("Hello from AuthActivity")

        if (FirebaseAuth.getInstance().currentUser != null) {
            viewModel.checkIsRegistrationComplete()
        }

        viewModel.isRegistrationComplete.observe(this , { isComplete ->
            if (isComplete) {
                Intent(this , MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        })
    }
}