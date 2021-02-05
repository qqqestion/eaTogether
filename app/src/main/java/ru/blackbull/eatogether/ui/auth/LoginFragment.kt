package ru.blackbull.eatogether.ui.auth

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.Resource

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()

        btnLoginCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnLoginConfirm.setOnClickListener { onClickLogin() }

    }

    private fun subscribeToObservers() {
        authViewModel.signInResult.observe(viewLifecycleOwner , Observer { signInResult ->
            when (signInResult) {
                is Resource.Success -> {
                    loginProgressBar.isVisible = false
                    findNavController().navigate(
                        R.id.action_loginFragment_to_mapFragment
                    )
                }
                is Resource.Error -> {
                    val msg = getString(R.string.errormessage_login_error)
                    Snackbar.make(requireView() , msg , Snackbar.LENGTH_SHORT).show()
                    loginProgressBar.isVisible = false
                }
                is Resource.Loading -> {
                    loginProgressBar.isVisible = true
                }
            }
        })

    }

    private fun onClickLogin() {
        val email = etLoginEmail.text.toString()
        val password = etLoginPassword.text.toString()
        var isOkay = true
        if (email.isEmpty() || email.isBlank()) {
            etLoginEmail.error = getString(R.string.errormessage_email_is_empty)
            isOkay = false
        }
        if (password.isEmpty() || password.isBlank()) {
            etLoginPassword.error = getString(R.string.errormessage_password_is_empty)
            isOkay = false
        }
        if (isOkay) {
            authViewModel.signIn(email , password)
        }
    }
}