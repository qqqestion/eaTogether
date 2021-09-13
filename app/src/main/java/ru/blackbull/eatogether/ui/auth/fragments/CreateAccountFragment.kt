package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_account.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.CreateAccountViewModel
import ru.blackbull.eatogether.ui.auth.UiState

@AndroidEntryPoint
class CreateAccountFragment : BaseFragment(R.layout.fragment_create_account) {

    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        btnRegistrationNext.setOnClickListener {
            applyAccountCreation()
        }
        etRegistrationPasswordConfirmation.setOnKeyListener { v , keyCode , event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                applyAccountCreation()
            }
            true
        }


        viewModel.createAccountStatus.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> showLoadingBar()
                is UiState.Failure -> {
                    snackbar(it.messageId)
                    hideLoadingBar()
                }
                UiState.Success -> {
                    hideLoadingBar()
                    findNavController().navigate(
                        CreateAccountFragmentDirections.actionCreateAccountFragmentToSetAccountInfoFragment()
                    )
                }
            }
        }
    }

    private fun applyAccountCreation() {
        viewModel.submitAccount(
            etRegistrationEmail.text.toString() ,
            etRegistrationPassword.text.toString() ,
            etRegistrationPasswordConfirmation.text.toString()
        )
    }
}