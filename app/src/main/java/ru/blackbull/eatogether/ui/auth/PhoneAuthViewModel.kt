package ru.blackbull.eatogether.ui.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.blackbull.eatogether.R
import javax.inject.Inject


@HiltViewModel
class PhoneAuthViewModel
@Inject constructor() : ViewModel() {

    private val verificationPhoneNumberData = MutableLiveData<UiState<Unit>>()
    private val verificationPhoneNumber: LiveData<UiState<Unit>> = verificationPhoneNumberData

    fun verifyPhoneNumber(phoneNumber: String) {
        if (Patterns.PHONE.matcher(phoneNumber).matches().not()) {
            verificationPhoneNumberData.value = failure(R.string.failure_phone_number_misformat)
        }
    }
}