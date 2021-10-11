package ru.blackbull.eatogether.ui.auth.fragments

sealed class AuthState {

    object AccountInfoSet : AuthState()
    object AccountNotInfoSet : AuthState()

    sealed class AuthError : AuthState() {

        object SignInError : AuthError()
        object SignUpError : AuthError()
        object UnknownError : AuthError()
    }
}
