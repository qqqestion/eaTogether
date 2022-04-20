package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_account.*
import ru.blackbull.domain.usecases.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.ui.auth.CreateAccountViewModel
import ru.blackbull.eatogether.ui.clearError
import ru.blackbull.eatogether.ui.onKeyEnter
import ru.blackbull.eatogether.ui.trimmedText

@AndroidEntryPoint
class CreateAccountFragment : BaseFragmentV2<CreateAccountViewModel>(
    R.layout.fragment_create_account, CreateAccountViewModel::class
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
            if (isLoading) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
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
            PasswordIsEmptyError -> TODO()
            PasswordsMismatchError -> TODO()
            EmailMalformedError -> TODO()
            UserAlreadyExists -> TODO()
            WeakPasswordError -> TODO()
            NoInternetError -> {
                showErrorDialog(R.string.error_no_internet)
            }
            UnexpectedNetworkCommunicationError -> TODO()
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