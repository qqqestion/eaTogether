package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.functional.Either
import javax.inject.Inject

class CheckAuthenticatedUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
) {

    suspend operator fun invoke(): Either<CheckAuthenticatedUseCaseError, Boolean> {
        return authDataSource.checkAuthenticated()
    }
}

sealed interface CheckAuthenticatedUseCaseError

sealed interface GetUserError

object UserNotFound : GetUserError
