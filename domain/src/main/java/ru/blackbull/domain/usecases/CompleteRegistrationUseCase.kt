package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthRepository
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.DomainAuthUser
import javax.inject.Inject

class CompleteRegistrationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        description: String,
        birthday: Long,
    ): Either<CompleteRegistrationUseCaseError, Unit> {
        // TODO: validation
        return authRepository.completeRegistration(DomainAuthUser(firstName, lastName, description, birthday, imageUrl = ""))
    }
}

sealed interface CompleteRegistrationUseCaseError

object FirstNameFormatError : CompleteRegistrationUseCaseError
object LastNameFormatError : CompleteRegistrationUseCaseError
object BirthdayError : CompleteRegistrationUseCaseError