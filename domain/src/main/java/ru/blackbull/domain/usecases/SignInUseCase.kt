package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthRepository
import ru.blackbull.domain.functional.Either
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(email: String, password: String): Either<SignInUseCaseError, Unit> {
        val isEmailInvalid = email.isEmailInvalid()
        val isPasswordInvalid = password.isPasswordInvalid()
        return when {
            isEmailInvalid && isPasswordInvalid -> Either.Left(EmailAndPasswordFormatError)
            isEmailInvalid -> Either.Left(EmailFormatError)
            isPasswordInvalid -> Either.Left(PasswordFormatError)
            else -> authRepository.signInWithEmailAndPassword(email, password)
        }
    }

    private fun String.isEmailInvalid(): Boolean = isEmpty()
    private fun String.isPasswordInvalid(): Boolean = isEmpty()
}

sealed interface SignInUseCaseError

object EmailFormatError : SignInUseCaseError
object PasswordFormatError : SignInUseCaseError
object EmailAndPasswordFormatError : SignInUseCaseError

sealed interface SignInError : SignInUseCaseError
object InvalidCredentials : SignInError