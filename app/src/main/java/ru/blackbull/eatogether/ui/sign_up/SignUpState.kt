package ru.blackbull.eatogether.ui.sign_up

import ru.blackbull.domain.usecases.SignUpUseCaseError

sealed class SignUpState {

    object Loading : SignUpState()
    data class Error(val error: SignUpUseCaseError) : SignUpState()
}
