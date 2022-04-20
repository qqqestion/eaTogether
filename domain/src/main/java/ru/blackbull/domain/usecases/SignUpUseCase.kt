package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.functional.map
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthDataSource,
) {

    suspend operator fun invoke(
        emailRaw: String,
        passwordRaw: String,
        confirmedPasswordRaw: String
    ): Either<SignUpUseCaseError, Unit> {
        val email = Email.from(emailRaw)
        val password = validatePassword(passwordRaw, confirmedPasswordRaw)
        return when  {
            email == null -> Either.Left(EmailMalformedError)
            password is Either.Right -> authRepository.createAccount(email.value, password.value)
            else -> password.map {  }
        }
    }

    private fun validatePassword(password: String, confirmedPassword: String): Either<SignUpUseCaseError, String> {
        return when {
            password.isEmpty() -> Either.Left(PasswordIsEmptyError)
            password != confirmedPassword -> Either.Left(PasswordsMismatchError)
            else -> Either.Right(password)
        }
    }
}

@JvmInline
value class Email private constructor(val value: String) {

    companion object {

        fun from(email: String): Email? {
            if (email.isEmpty()) return null
            return Email(email)
        }
    }
}

sealed interface SignUpUseCaseError
object PasswordIsEmptyError : SignUpUseCaseError
object PasswordsMismatchError : SignUpUseCaseError

sealed interface SignUpError : SignUpUseCaseError
object WeakPasswordError : SignUpError
object EmailMalformedError : SignUpError
object UserAlreadyExists : SignUpError