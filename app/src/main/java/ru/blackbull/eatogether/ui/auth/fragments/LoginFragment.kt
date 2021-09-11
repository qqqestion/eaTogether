package ru.blackbull.eatogether.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.SignInViewModel
import ru.blackbull.eatogether.ui.auth.UiState
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()
        Timber.d("View created")

        btnLoginConfirm.setOnClickListener {
            applySignIn()
        }
        etLoginPassword.setOnKeyListener { v , keyCode , event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                applySignIn()
            }
            true
        }
    }

    private fun applySignIn() {
        viewModel.signIn(
            etLoginEmail.text.toString() ,
            etLoginPassword.text.toString()
        )
    }

    private fun subscribeToObservers() {
        viewModel.signInStatus.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                    snackbar(it.messageId)
                    hideLoadingBar()
                }
                UiState.Loading -> {
                    showLoadingBar()
                }
                UiState.Success -> {
                    hideLoadingBar()
                    Intent(requireContext() , MainActivity::class.java).apply {
                        startActivity(this)
                        requireActivity().finish()
                    }
                }
            }
        }
    }
}