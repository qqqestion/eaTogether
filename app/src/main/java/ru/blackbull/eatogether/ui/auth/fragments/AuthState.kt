package ru.blackbull.eatogether.ui.auth.fragments

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
