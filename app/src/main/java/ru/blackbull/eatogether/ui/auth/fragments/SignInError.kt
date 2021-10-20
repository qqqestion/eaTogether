package ru.blackbull.eatogether.ui.auth.fragments

sealed class SignInError {

    object EmailOrPasswordAreWrong : SignInError()
    object Unknown : SignInError()
}
