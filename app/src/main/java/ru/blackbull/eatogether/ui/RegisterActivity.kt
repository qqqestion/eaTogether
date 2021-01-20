package ru.blackbull.eatogether.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.activity_register.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.state.RegistrationState
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var isFirstStepCompleted = false

    private val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseViewModel.signUpResult.observe(this , Observer { signUpResult ->
            when (signUpResult) {
                is RegistrationState.Error -> {
                    when (signUpResult.exception) {
                        is FirebaseAuthWeakPasswordException -> {
                            shortToast("Weak password")
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            shortToast("Email address is malformed")
                        }
                        is FirebaseAuthUserCollisionException -> {
                            shortToast("account with the given email address already exists")
                        }
                    }
                }
                is RegistrationState.Success -> {
                    val intent = Intent(this , ProfileActivity::class.java)
                    startActivity(intent)
                }
                is RegistrationState.Loading -> {
                    shortToast("loading")
                }
            }
        })

        register_next_and_confirm!!.setOnClickListener { view: View? ->
            onClickNext(view)
        }
        register_cancel_and_back!!.setOnClickListener { view: View? ->
            onClickCancel(view)
        }
    }

    private fun onClickNext(view: View?) {
        if (!isFirstStepCompleted) {
            onStepForward()
        } else {
            val userInfo = User()
            userInfo.email = register_email_field.text.toString()
            userInfo.firstName = register_first_name_field.text.toString()
            userInfo.lastName = register_last_name_field.text.toString()
            userInfo.description = register_description_field.text.toString()

            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd" , Locale.US)
                val date = formatter.parse(register_birthday_field.text.toString())
                userInfo.birthday = Timestamp(date!!)
            } catch (e: ParseException) {
                register_birthday_field.error = "Введена некоректная дата"
                return
            }

            val pwd = register_password_field.text.toString()
            val confirmPwd = register_password_confirmation_field.text.toString()
            if (pwd != confirmPwd) {
                register_password_confirmation_field.error = "Пароли не совпадают"
                return
            }
//            firebaseViewModel.signUpWithEmailAndPassword(userInfo , pwd)
        }
    }

    private fun onClickCancel(view: View?) {
        if (isFirstStepCompleted) {
            onStepBack()
        } else {
            finish()
        }
    }

    private fun onStepForward() {
        register_first_step.visibility = View.GONE
        register_second_step.visibility = View.VISIBLE
        isFirstStepCompleted = true
        register_next_and_confirm!!.setText(R.string.register)
        register_cancel_and_back!!.setText(R.string.back)
    }

    private fun onStepBack() {
        register_first_step.visibility = View.VISIBLE
        register_second_step.visibility = View.GONE
        isFirstStepCompleted = false
        register_next_and_confirm!!.setText(R.string.next)
        register_cancel_and_back!!.setText(R.string.cancel)
    }

    override fun onBackPressed() {
        if (isFirstStepCompleted) {
            onStepBack()
        } else {
            super.onBackPressed()
        }
    }
}