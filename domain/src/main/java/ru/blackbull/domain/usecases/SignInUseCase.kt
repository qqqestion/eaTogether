package ru.blackbull.domain.usecases

import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.exceptions.EmailValidationException
import ru.blackbull.domain.exceptions.PasswordValidationException
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthDataSource ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<SignInUseCase.Params , Unit>(dispatchers) {

    override suspend fun doWork(params: Params) {
        if (params.email.isEmailValid().not()) {
            throw EmailValidationException()
        }
        if (params.email.isPasswordValid().not()) {
            throw PasswordValidationException()
        }
        authRepository.signInWithEmailAndPassword(params.email , params.password)
    }

    private fun String.isEmailValid(): Boolean {
        if (isEmpty()) return false
        return true
    }

    private fun String.isPasswordValid(): Boolean {
        if (isEmpty()) return false
        return true
    }

    data class Params(val email: String , val password: String)
}