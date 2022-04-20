package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.DomainAuthUser
import javax.inject.Inject

class CompleteRegistrationUseCase @Inject constructor(
    private val authRepository: AuthDataSource,
) {

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        description: String,
        birthday: Long,
    ): Either<CompleteRegistrationUseCaseError, Unit> {
        // TODO: validation
        return authRepository.setAccountInfo(DomainAuthUser(firstName, lastName, description, birthday, imageUrl = ""))
    }
}

sealed interface CompleteRegistrationUseCaseError

object FirstNameFormatError : CompleteRegistrationUseCaseError
object LastNameFormatError : CompleteRegistrationUseCaseError
object BirthdayError : CompleteRegistrationUseCaseError