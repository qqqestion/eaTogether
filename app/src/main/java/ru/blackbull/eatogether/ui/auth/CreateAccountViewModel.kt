package ru.blackbull.eatogether.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.exceptions.ConfirmPasswordException
import ru.blackbull.domain.exceptions.EmailValidationException
import ru.blackbull.domain.exceptions.PasswordValidationException
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.usecases.SignUpUseCase
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.ui.auth.fragments.CreateAccountFragmentDirections
import ru.blackbull.eatogether.ui.auth.fragments.SignUpState
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val signUp: SignUpUseCase
) : BaseViewModel() {

    private val _state = MutableLiveData<SignUpState>()
    val state: LiveData<SignUpState> = _state

    fun submitAccount(
        email: String ,
        password: String ,
        confirmedPassword: String
    ) = viewModelScope.launch {
        when (val result = signUp(email, password, confirmedPassword)) {
            is Either.Left -> _state.value = SignUpState.Error(result.error)
            is Either.Right -> navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToSetAccountInfoFragment())
        }
    }

    private fun getCreateAccountError(t: Throwable): Int = when (t) {
        is FirebaseAuthWeakPasswordException -> R.string.error_weak_password
        is FirebaseAuthInvalidCredentialsException -> R.string.error_email_malformed
        is FirebaseAuthUserCollisionException -> R.string.error_email_already_exists
        is EmailValidationException -> R.string.error_email_is_empty
        is PasswordValidationException -> R.string.error_password_is_empty
        is ConfirmPasswordException -> R.string.error_passwords_mismatch
        else -> R.string.error_default
    }
}