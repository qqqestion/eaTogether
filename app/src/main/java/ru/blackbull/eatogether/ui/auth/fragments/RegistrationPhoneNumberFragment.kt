package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_registration_phone_number.*
import ru.blackbull.eatogether.BuildConfig
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class RegistrationPhoneNumberFragment : BaseFragment(R.layout.fragment_registration_phone_number) {

    private lateinit var verificationId: String
    private lateinit var token: PhoneAuthProvider.ForceResendingToken

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        Timber.d("onViewCreated")
        btnRegistrationNext.setOnClickListener {
            val phone = etRegistrationPhoneNumber.text.toString()
            Timber.d("Phone number: $phone")
            val response = SafetyNet.getClient(requireActivity())
                .verifyWithRecaptcha(BuildConfig.RECAPTCHA_API_KEY)
                .addOnSuccessListener(requireActivity()) { response ->
                    // Indicates communication with reCAPTCHA service was
                    // successful.
                    val userResponseToken = response.tokenResult
                    if (response.tokenResult?.isNotEmpty() == true) {
                        // Validate the user response token using the
                        // reCAPTCHA siteverify API.
                        verifyPhoneNumber(phone)
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    if (e is ApiException) {
                        // An error occurred when communicating with the
                        // reCAPTCHA service. Refer to the status code to
                        // handle the error appropriately.
                        Timber.d(
                            "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}"
                        )
                        showErrorDialog(CommonStatusCodes.getStatusCodeString(e.statusCode))
                    } else {
                        // A different, unknown type of error occurred.
                        Timber.d("Error: ${e.message}")
                        showErrorDialog(e.message.toString())
                    }
                }
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
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Timber.d(e , "onVerificationFailed")

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Timber.d("Invalid request")
                    showErrorDialog("Неправильный запрос")
                } else if (e is FirebaseTooManyRequestsException) {
                    Timber.d("SMS quota for the project has been exceeded")
                    showErrorDialog("СМС квота израсходована")
                }
            }

            override fun onCodeSent(
                verificationId: String ,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@RegistrationPhoneNumberFragment.token = token
                this@RegistrationPhoneNumberFragment.verificationId = verificationId
                Timber.d("onCodeSent:$verificationId")
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
            .setTimeout(60L , TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        Timber.d("Verifying phone number")
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}