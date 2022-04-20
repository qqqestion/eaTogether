package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.functional.Either
import javax.inject.Inject

class CheckRegistrationCompleteUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
) {

    suspend operator fun invoke(): Either<NetworkError, Boolean> = authDataSource.isAccountInfoSet()

}

sealed interface CheckRegistrationCompleteUseCaseError