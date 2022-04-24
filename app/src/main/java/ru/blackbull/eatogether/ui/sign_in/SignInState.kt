package ru.blackbull.eatogether.ui.sign_in

import ru.blackbull.domain.usecases.SignInUseCaseError

sealed class SignInState {

    object Loading : SignInState()

    data class Error(val error: SignInUseCaseError) : SignInState()
}
