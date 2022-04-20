package ru.blackbull.eatogether.ui.auth.fragments

import ru.blackbull.domain.usecases.SignInUseCaseError
import ru.blackbull.domain.usecases.SignUpUseCaseError

sealed class SignInState {

    object Loading : SignInState()

    data class Error(val error: SignInUseCaseError) : SignInState()
}

sealed class SignUpState {

    object Loading : SignUpState()
    data class Error(val error: SignUpUseCaseError) : SignUpState()
}
