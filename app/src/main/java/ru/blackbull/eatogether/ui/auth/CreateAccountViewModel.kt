package ru.blackbull.eatogether.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.exceptions.ConfirmPasswordException
import ru.blackbull.domain.exceptions.EmailValidationException
import ru.blackbull.domain.exceptions.PasswordValidationException
import ru.blackbull.domain.usecases.CreateAccountUseCase
import ru.blackbull.eatogether.R
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val createAccount: CreateAccountUseCase
) : ViewModel() {

    private val _createAccountStatus = MutableLiveData<UiState>()
    val createAccountStatus: LiveData<UiState> = _createAccountStatus

    fun submitAccount(
        email: String ,
        password: String ,
        confirmedPassword: String
    ) = viewModelScope.launch {
        createAccount.invoke(
            CreateAccountUseCase.Params(email , password , confirmedPassword)
        ).onSuccess {
            _createAccountStatus.value = success()
        }.onFailure {
            _createAccountStatus.value = failure(getCreateAccountError(it))
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