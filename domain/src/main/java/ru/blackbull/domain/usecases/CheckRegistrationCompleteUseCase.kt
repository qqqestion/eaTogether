package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthRepository
import ru.blackbull.domain.functional.Either
import javax.inject.Inject

class CheckRegistrationCompleteUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(): Either<NetworkError, Boolean> = authRepository.isAccountInfoSet()

}

sealed interface CheckRegistrationCompleteUseCaseError