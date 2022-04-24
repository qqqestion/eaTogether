package ru.blackbull.eatogether.ui.complete_registration

import ru.blackbull.domain.usecases.CompleteRegistrationUseCaseError

sealed class CompleteRegistrationState {

    object Loading : CompleteRegistrationState()

    data class Error(val error: CompleteRegistrationUseCaseError) : CompleteRegistrationState()
}