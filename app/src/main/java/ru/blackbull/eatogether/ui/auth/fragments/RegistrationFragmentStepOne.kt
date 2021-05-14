package ru.blackbull.eatogether.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration_step_one.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.auth.AuthViewModel
import ru.blackbull.eatogether.ui.main.MainActivity
import ru.blackbull.eatogether.ui.main.snackbar

@AndroidEntryPoint
class RegistrationFragmentStepOne : Fragment(R.layout.fragment_registration_step_one) {

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        btnRegistrationNext.setOnClickListener {
            val email = etRegistrationEmail.text.toString()
            if (email.isEmpty()) {
                etRegistrationEmail.error =
                    requireContext().getString(R.string.errormessage_email_is_empty)
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etRegistrationEmail.error =
                    requireContext().getString(R.string.errormessage_email_malformed)
                return@setOnClickListener
            }
            val password = etRegistrationPassword.text.toString()
            if (password.isEmpty()) {
                etRegistrationPassword.error =
                    requireContext().getString(R.string.errormessage_password_is_empty)
                return@setOnClickListener
            }
            if (password != etRegistrationPasswordConfirmation.text.toString()) {
                etRegistrationPassword.error =
                    requireContext().getString(R.string.errormessage_passwords_mismatch)
                etRegistrationPasswordConfirmation.error =
                    requireContext().getString(R.string.errormessage_passwords_mismatch)
                return@setOnClickListener
            }
            findNavController().navigate(
                RegistrationFragmentStepOneDirections.actionRegistrationStepOneFragmentToRegistrationStepTwoFragment(
                    email , password
                )
            )
        }
    }
}