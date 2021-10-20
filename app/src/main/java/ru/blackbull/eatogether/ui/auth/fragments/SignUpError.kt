package ru.blackbull.eatogether.ui.auth.fragments

sealed class SignUpError {

    object WeakPassword : SignUpError()
    object EmailMalformed : SignUpError()
    object EmailCollision : SignUpError()
}
