package ru.blackbull.eatogether.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_registration_verification_phone.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.AuthViewModel
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber

class RegistrationVerificationPhoneFragment :
    BaseFragment(R.layout.fragment_registration_verification_phone) {

    private val viewModel: AuthViewModel by viewModels()

    private val args: RegistrationVerificationPhoneFragmentArgs by navArgs()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        btnRegistrationNext.setOnClickListener {
            val userCode = pinView.text.toString().trim()
            if (userCode.isNotEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    args.verificationId , userCode
                )
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            viewModel.checkIsRegistrationComplete()
                        } else {
                            Timber.d(task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                showErrorDialog("Ошибка Firebase")
                            } else {
                                showErrorDialog(task.exception?.message.toString())
                            }
                        }
                    }
            } else {
                snackbar("Введите код")
            }
        }
        viewModel.isRegistrationComplete.observe(viewLifecycleOwner , { isComplete ->
            if (isComplete) {
                Intent(requireActivity() , MainActivity::class.java).also {
                    requireActivity().startActivity(it)
                    requireActivity().finish()
                }
            } else {
                findNavController().navigate(
                    RegistrationVerificationPhoneFragmentDirections.actionRegistrationVerificationPhoneFragmentToRegistrationStepTwoFragment()
                )
            }
        })
    }
}