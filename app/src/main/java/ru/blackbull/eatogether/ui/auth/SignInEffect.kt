package ru.blackbull.eatogether.ui.auth

sealed class SignInEffect {

    object NavigateToSetAccountInfo : SignInEffect()
    object NavigateToMain : SignInEffect()
}
