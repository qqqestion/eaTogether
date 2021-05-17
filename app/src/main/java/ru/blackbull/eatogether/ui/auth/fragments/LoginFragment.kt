package ru.blackbull.eatogether.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.AuthViewModel
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()

        Timber.d("View created")

        btnLoginConfirm.setOnClickListener { onClickLogin() }
    }

    private fun subscribeToObservers() {
        authViewModel.signInResult.observe(viewLifecycleOwner , EventObserver(
            onError = {
                val msg = getString(R.string.errormessage_login_error)
//                showErrorDialog(msg)
                hideLoadingBar()
                snackbar(it)
            } ,
            onLoading = {
                showLoadingBar()
            }
        ) {
            hideLoadingBar()
            Intent(requireContext() , MainActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
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