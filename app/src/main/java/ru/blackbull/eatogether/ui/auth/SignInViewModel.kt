package ru.blackbull.eatogether.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.functional.onFailure
import ru.blackbull.domain.functional.onSuccess
import ru.blackbull.domain.usecases.IsAccountInfoSetUseCase
import ru.blackbull.domain.usecases.SignInUseCase
import ru.blackbull.eatogether.ui.auth.fragments.AuthState
import ru.blackbull.eatogether.ui.auth.fragments.SignInError
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signIn: SignInUseCase ,
    private val isAccountInfoSet: IsAccountInfoSetUseCase
) : ViewModel() {

    private val _signInStatus = MutableLiveData<AuthState>()
    val signInStatus: LiveData<AuthState> = _signInStatus

    private val effectsData = MutableSharedFlow<SignInEffect>(
        replay = 0 ,
        extraBufferCapacity = 1 ,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects: SharedFlow<SignInEffect> = effectsData.asSharedFlow()

    fun signIn(email: String , password: String) = viewModelScope.launch {
        _signInStatus.value = AuthState.Loading
        when (val signInStatus = signIn.invoke(SignInUseCase.Params(email , password))) {
            is Either.Left -> {
                _signInStatus.value = AuthState.SignInFailure(getSignInError(signInStatus.a))
            }
            is Either.Right -> {
                isAccountInfoSet.invoke(UseCase.None)
                    .onFailure {
                        // TODO: what state should be here
//                            _signInStatus.value = AuthState.
                    }.onSuccess { isInfoSet ->
                        val effect =
                            if (isInfoSet) SignInEffect.NavigateToMain
                            else SignInEffect.NavigateToSetAccountInfo
                        effectsData.tryEmit(effect)
                        _signInStatus.value = AuthState.SignInSuccessfully
                    }
            }
        }
    }

    private fun getSignInError(t: Throwable): SignInError = when (t) {
        is FirebaseAuthInvalidUserException , is FirebaseAuthInvalidCredentialsException -> SignInError.EmailOrPasswordAreWrong
        else -> SignInError.Unknown
    }
}