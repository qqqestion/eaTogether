package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_registration_phone_number.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.PhoneAuthViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RegistrationPhoneNumberFragment : BaseFragment(R.layout.fragment_registration_phone_number) {

    private lateinit var verificationId: String
    private lateinit var token: PhoneAuthProvider.ForceResendingToken

    private val viewModel: PhoneAuthViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        Timber.d("onViewCreated")
        btnRegistrationNext.setOnClickListener {
            val phone = etRegistrationPhoneNumber.text.toString()
            Timber.d("Phone number: $phone")
            verifyPhoneNumber(phone)
            Timber.d("Validation complete")
        }
    }

    private fun verifyPhoneNumber(phone: String) {
        val auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
        Timber.d("Start verifying")
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Timber.d("onVerificationCompleted:$credential")
                hideLoadingBar()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Timber.d(e , "onVerificationFailed")
                hideLoadingBar()

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        Timber.d("Invalid request")
                        showErrorDialog("Неправильный запрос")
                    }
                    is FirebaseTooManyRequestsException -> {
                        Timber.d("SMS quota for the project has been exceeded")
                        showErrorDialog("СМС квота израсходована")
                    }
                    else -> {
                        showErrorDialog("Ошибка, попробуйте еще раз")
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String ,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@RegistrationPhoneNumberFragment.token = token
                this@RegistrationPhoneNumberFragment.verificationId = verificationId
                Timber.d("onCodeSent:$verificationId")
                hideLoadingBar()
                findNavController().navigate(
                    RegistrationPhoneNumberFragmentDirections
                        .actionRegistrationPhoneNumberFragmentToRegistrationVerificationPhoneFragment(
                            verificationId
                        )
                )
            }
        }
        Timber.d("Making request for phone auth")
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)       // Phone number to verify
            .setTimeout(30L , TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        Timber.d("Verifying phone number")
        showLoadingBar()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}