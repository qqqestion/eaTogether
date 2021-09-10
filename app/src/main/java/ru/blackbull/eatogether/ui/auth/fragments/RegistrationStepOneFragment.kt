package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration_step_one.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment

@AndroidEntryPoint
class RegistrationStepOneFragment : BaseFragment(R.layout.fragment_registration_step_one) {

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        btnRegistrationNext.setOnClickListener {
            val email = etRegistrationEmail.text.toString()
            if (email.isEmpty()) {
                etRegistrationEmail.error =
                    requireContext().getString(R.string.error_email_is_empty)
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etRegistrationEmail.error =
                    requireContext().getString(R.string.error_email_malformed)
                return@setOnClickListener
            }
            val password = etRegistrationPassword.text.toString()
            if (password.isEmpty()) {
                etRegistrationPassword.error =
                    requireContext().getString(R.string.error_password_is_empty)
                return@setOnClickListener
            }
            if (password != etRegistrationPasswordConfirmation.text.toString()) {
                etRegistrationPassword.error =
                    requireContext().getString(R.string.error_passwords_mismatch)
                etRegistrationPasswordConfirmation.error =
                    requireContext().getString(R.string.error_passwords_mismatch)
                return@setOnClickListener
            }
//            findNavController().navigate(
//                RegistrationFragmentStepOneDirections.actionRegistrationStepOneFragmentToRegistrationStepTwoFragment(
//                    email , password
//                )
//            )
        }
    }
}