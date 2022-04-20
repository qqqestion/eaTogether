package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import ru.blackbull.domain.usecases.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.ui.auth.SignInViewModel
import ru.blackbull.eatogether.ui.clearError
import ru.blackbull.eatogether.ui.onKeyEnter
import ru.blackbull.eatogether.ui.setErrorMessage
import ru.blackbull.eatogether.ui.trimmedText

@AndroidEntryPoint
class SignInFragment : BaseFragmentV2<SignInViewModel>(
    R.layout.fragment_login, SignInViewModel::class
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, ::handleState)

        btnLoginConfirm.setOnClickListener {
            viewModel.signIn(etLoginEmail.trimmedText, etLoginPassword.trimmedText)
        }
        etLoginPassword.onKeyEnter {
            hideKeyboard()
            viewModel.signIn(etLoginEmail.trimmedText, etLoginPassword.trimmedText)
        }
    }

    private fun handleState(state: SignInState) {
        val isLoading = (state is SignInState.Loading)

        btnLoginConfirm.isEnabled = isLoading.not()
        progressBar.isVisible = isLoading
        when (state) {
            is SignInState.Error -> handleError(state.error)
            else -> {
                tilLoginEmail.clearError()
                tilLoginPassword.clearError()
            }
        }
    }

    private fun handleError(error: SignInUseCaseError) {
        when (error) {
            EmailAndPasswordFormatError -> {
                tilLoginEmail.setErrorMessage(getString(R.string.error_email_malformed))
                tilLoginPassword.setErrorMessage(getString(R.string.error_password_is_empty))
            }
            EmailFormatError -> {
                tilLoginEmail.setErrorMessage(getString(R.string.error_email_malformed))
            }
            PasswordFormatError -> {
                tilLoginPassword.setErrorMessage(getString(R.string.error_password_is_empty))
            }
            InvalidCredentials -> {
                snackbar(R.string.error_sign_in_failed)
            }
            NoInternetError -> {
                showErrorDialog(R.string.error_no_internet)
            }
            UnexpectedNetworkCommunicationError -> TODO()
        }
    }
}