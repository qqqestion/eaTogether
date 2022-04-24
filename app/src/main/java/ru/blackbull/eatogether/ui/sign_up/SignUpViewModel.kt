package ru.blackbull.eatogether.ui.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.usecases.SignUpUseCase
import ru.blackbull.eatogether.core.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUp: SignUpUseCase
) : BaseViewModel() {

    private val _state = MutableLiveData<SignUpState>()
    val state: LiveData<SignUpState> = _state

    fun submitAccount(
        email: String ,
        password: String ,
        confirmedPassword: String
    ) = viewModelScope.launch {
        _state.value = SignUpState.Loading
        when (val result = signUp(email, password, confirmedPassword)) {
            is Either.Left -> _state.value = SignUpState.Error(result.error)
            is Either.Right -> navigate(SignUpFragmentDirections.actionCreateAccountFragmentToSetAccountInfoFragment())
        }
    }
}