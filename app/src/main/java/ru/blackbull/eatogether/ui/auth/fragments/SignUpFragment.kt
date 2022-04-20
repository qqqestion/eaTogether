package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_account.*
import ru.blackbull.domain.usecases.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.ui.auth.SignUpViewModel
import ru.blackbull.eatogether.ui.clearError
import ru.blackbull.eatogether.ui.onKeyEnter
import ru.blackbull.eatogether.ui.setErrorMessage
import ru.blackbull.eatogether.ui.trimmedText

@AndroidEntryPoint
class SignUpFragment : BaseFragmentV2<SignUpViewModel>(
    R.layout.fragment_create_account, SignUpViewModel::class
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRegistrationNext.setOnClickListener {
            applyAccountCreation()
        }
        etRegistrationPasswordConfirmation.onKeyEnter {
            hideKeyboard()
            applyAccountCreation()
        }


        viewModel.state.observe(viewLifecycleOwner) { state ->
            val isLoading = (state is SignUpState.Loading)

            btnRegistrationNext.isEnabled = isLoading.not()
            progressBar.isVisible = isLoading
            when (state) {
                is SignUpState.Error -> handleError(state.error)
                else -> {
                    tilRegistrationEmail.clearError()
                    tilRegistrationPassword.clearError()
                    tilRegistrationPasswordConfirm.clearError()
                }
            }
        }
    }

    private fun handleError(error: SignUpUseCaseError) {
        when (error) {
            PasswordIsEmptyError -> {
                tilRegistrationPassword.setErrorMessage(getString(R.string.error_password_is_empty))
            }
            PasswordsMismatchError -> {
                tilRegistrationPassword.setErrorMessage(getString(R.string.error_passwords_mismatch))
                tilRegistrationPasswordConfirm.setErrorMessage(getString(R.string.error_passwords_mismatch))
            }
            EmailMalformedError -> {
                tilRegistrationEmail.setErrorMessage(getString(R.string.error_email_malformed))
            }
            UserAlreadyExists -> {
                showErrorDialog(R.string.error_user_already_exists)
            }
            WeakPasswordError -> {
                tilRegistrationPassword.setErrorMessage(getString(R.string.error_weak_password))
            }
            NoInternetError -> {
                showErrorDialog(R.string.error_no_internet)
            }
            UnexpectedNetworkCommunicationError -> {
                TODO()
            }
        }
    }

    private fun applyAccountCreation() {
        viewModel.submitAccount(
            etRegistrationEmail.trimmedText,
            etRegistrationPassword.trimmedText,
            etRegistrationPasswordConfirmation.trimmedText
        )
    }
}