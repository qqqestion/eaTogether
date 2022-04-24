package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthRepository
import ru.blackbull.domain.functional.Either
import javax.inject.Inject

class CheckAuthenticatedUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(): Either<CheckAuthenticatedUseCaseError, Boolean> {
        return authRepository.checkAuthenticated()
    }
}

sealed interface CheckAuthenticatedUseCaseError

sealed interface GetUserError

object UserNotFound : GetUserError
