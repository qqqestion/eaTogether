package ru.blackbull.eatogether.ui.auth.fragments

import ru.blackbull.domain.usecases.SignInUseCaseError
import ru.blackbull.domain.usecases.SignUpUseCaseError

sealed class AuthState {

    object Loading : AuthState()

    object SignInSuccessfully : AuthState()
    object SignUpSuccessfully : AuthState()

    sealed class InputFailure : AuthState() {
        object Email : InputFailure()
        object Password : InputFailure()
    }

    data class SignInFailure(val error: SignInError) : AuthState()
    data class SignUpFailure(val error: SignUpError) : AuthState()
}

sealed class SignInState {

    object Loading : SignInState()

    data class Error(val error: SignInUseCaseError) : SignInState()
}

sealed class SignUpState {

    object Loading : SignUpState()

    data class Error(val error: SignUpUseCaseError) : SignUpState()
}
