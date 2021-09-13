package ru.blackbull.eatogether.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.exceptions.EmailValidationException
import ru.blackbull.domain.exceptions.PasswordValidationException
import ru.blackbull.domain.usecases.SignInUseCase
import ru.blackbull.eatogether.R
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signIn: SignInUseCase
) : ViewModel() {

    private val _signInStatus = MutableLiveData<UiState>()
    val signInStatus: LiveData<UiState> = _signInStatus

    fun signIn(email: String , password: String) = viewModelScope.launch {
        signInStatus.value?.let { if (it is UiState.Loading) return@launch }
        _signInStatus.value = loading()
        signIn.invoke(SignInUseCase.Params(email , password) , viewModelScope) {
            it.fold(
                { t -> _signInStatus.value = failure(getSignInError(t)) } ,
                { _signInStatus.value = success() }
            )
        }
    }

    private fun getSignInError(t: Throwable): Int = when (t) {
        is FirebaseAuthInvalidUserException -> R.string.error_sign_in_failed
        is FirebaseAuthInvalidCredentialsException -> R.string.error_sign_in_failed
        is EmailValidationException -> R.string.error_email_malformed
        is PasswordValidationException -> R.string.error_password_is_empty
        else -> R.string.error_default
    }
}