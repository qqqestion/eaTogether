package ru.blackbull.eatogether.ui.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.usecases.CheckRegistrationCompleteUseCase
import ru.blackbull.domain.usecases.SignInUseCase
import ru.blackbull.eatogether.core.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signIn: SignInUseCase,
    private val checkRegistrationComplete: CheckRegistrationCompleteUseCase
) : BaseViewModel() {

    private val _state = MutableLiveData<SignInState>()
    val state: LiveData<SignInState> = _state

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _state.value = SignInState.Loading
        when (val signInStatus = signIn.invoke(email, password)) {
            is Either.Left -> _state.value = SignInState.Error(signInStatus.error)
            is Either.Right -> checkIfProfileComplete()
        }
    }

    private suspend fun checkIfProfileComplete() {
        when (val check = checkRegistrationComplete.invoke()) {
            is Either.Left -> _state.value = SignInState.Error(check.error)
            is Either.Right -> {
                val destination =
                    if (check.value) SignInFragmentDirections.actionLoginFragmentToMapFragment()
                    else SignInFragmentDirections.actionLoginFragmentToSetAccountInfoFragment()
                navigate(destination)
            }
        }
    }
}