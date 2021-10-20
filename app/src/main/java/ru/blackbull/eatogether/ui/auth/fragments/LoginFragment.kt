package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.SignInEffect
import ru.blackbull.eatogether.ui.auth.SignInViewModel
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        viewModel.signInStatus.observe(viewLifecycleOwner , ::handleState)
        Timber.d("View created")

        btnLoginConfirm.setOnClickListener {
            applySignIn()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect(::handleEffect)
            }
        }
        etLoginPassword.setOnKeyListener { v , keyCode , event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                hideKeyboard()
                applySignIn()
                true
            } else {
                false
            }
        }
    }

    private fun handleEffect(effect: SignInEffect): Unit = when (effect) {
        SignInEffect.NavigateToMain -> findNavController()
            .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment())
        SignInEffect.NavigateToSetAccountInfo -> findNavController()
            .navigate(LoginFragmentDirections.actionLoginFragmentToSetAccountInfoFragment())
    }

    private fun applySignIn() {
        viewModel.signIn(
            etLoginEmail.text.toString() ,
            etLoginPassword.text.toString()
        )
    }

    private fun handleState(authState: AuthState) {
        val isLoading = (authState is AuthState.Loading)

        btnLoginConfirm.isEnabled = isLoading.not()
        progressBar.isVisible = isLoading
    }

    private fun getMessageId(error: SignInError): Int = when (error) {
        SignInError.EmailOrPasswordAreWrong -> R.string.error_sign_in_failed
        SignInError.Unknown -> R.string.error_default
    }
}