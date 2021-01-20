package ru.blackbull.eatogether.ui.auth

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.Status

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val authViewModel: AuthViewModel by viewModels()

    private var isFirstStepCompleted = false

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()

        btnRegistrationNextAndConfirm.setOnClickListener { onClickNext() }
        btnRegistrationCancelAndBack.setOnClickListener { onClickCancel() }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFirstStepCompleted) {
                    onStepBack()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner , callback)
    }

    private fun subscribeToObservers() {
        authViewModel.signUpResult.observe(viewLifecycleOwner , Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    btnRegistrationNextAndConfirm.isEnabled = true
                    findNavController().navigate(
                        R.id.action_registrationFragment_to_mapFragment
                    )
                }
                Status.ERROR -> {
                    val stringId = when (result.error) {
                        is FirebaseAuthWeakPasswordException ->
                            R.string.errormessage_weak_password
                        is FirebaseAuthInvalidCredentialsException ->
                            R.string.errormessage_email_malformed
                        is FirebaseAuthUserCollisionException ->
                            R.string.errormessage_user_already_exists
                        else -> null
                    }
                    val msg = if (stringId == null) {
                        result?.msg ?: getString(R.string.errormessage_unknown_error)
                    } else {
                        getString(stringId)
                    }

                    Snackbar.make(
                        requireView() ,
                        msg ,
                        Snackbar.LENGTH_LONG
                    ).show()
                    btnRegistrationNextAndConfirm.isEnabled = true
                }
                Status.LOADING -> {
                    btnRegistrationNextAndConfirm.isEnabled = false
                }
            }
        })
    }

    private fun onClickNext() {
        if (!isFirstStepCompleted) {
            onStepForward()
        } else {
            authViewModel.signUp(
                etRegistrationEmail.text.toString() ,
                etRegistrationPassword.text.toString() ,
                etRegistrationFirstName.text.toString() ,
                etRegistrationLastName.text.toString() ,
                etRegistrationBirthday.text.toString() ,
                etRegistrationDescription.text.toString()
            )
        }
    }

    private fun onClickCancel() {
        if (isFirstStepCompleted) {
            onStepBack()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun onStepForward() {
        val password = etRegistrationPassword.text.toString()
        if (password != etRegistrationPasswordConfirmation.text.toString()) {
            Snackbar.make(
                requireView() ,
                getString(R.string.errormessage_passwords_mismatch) ,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }
        llRegistrationFirstStep.visibility = View.GONE
        llRegistrationSecondStep.visibility = View.VISIBLE
        isFirstStepCompleted = true
        btnRegistrationNextAndConfirm.text = getString(R.string.register)
        btnRegistrationCancelAndBack.text = getString(R.string.back)
    }

    private fun onStepBack() {
        llRegistrationFirstStep.visibility = View.VISIBLE
        llRegistrationSecondStep.visibility = View.GONE
        isFirstStepCompleted = false
        btnRegistrationNextAndConfirm.text = getString(R.string.next)
        btnRegistrationCancelAndBack.text = getString(R.string.cancel)
    }
}