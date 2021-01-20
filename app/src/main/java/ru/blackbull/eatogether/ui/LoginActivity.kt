package ru.blackbull.eatogether.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.other.Status
import ru.blackbull.eatogether.ui.auth.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity: "

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_cancel_btn.setOnClickListener { finish() }
        login_confirm_btn.setOnClickListener { view: View? ->
            onClickLogin(view)
        }
        authViewModel.signInResult.observe(this , Observer { signInResult ->
            when (signInResult.status) {
                Status.SUCCESS -> {

                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })
    }

    private fun onClickLogin(view: View?) {
        val email = login_email_field.text.toString()
        val password = login_password_field.text.toString()
        if (email.isEmpty() || email.isBlank()) {
            login_email_field.error = "Введите почту"
            return
        }
        if (password.isEmpty() || password.isBlank()) {
            login_password_field.error = "Введите пароль"
            return
        }
        authViewModel.signIn(email , password)
    }
}