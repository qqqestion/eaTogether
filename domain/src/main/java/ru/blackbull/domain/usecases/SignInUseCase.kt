package ru.blackbull.domain.usecases

import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.UserAuthValidator
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthDataSource ,
    private val userAuthValidator: UserAuthValidator ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<SignInUseCase.Params , Unit>(dispatchers) {

    override suspend fun doWork(params: Params) {
        userAuthValidator.validateEmail(params.email)
        userAuthValidator.validatePassword(params.password)
        authRepository.signInWithEmailAndPassword(params.email , params.password)
    }

    private fun String.isPasswordValid(): Boolean {
        if (isEmpty()) return false
        return true
    }

    data class Params(val email: String , val password: String)
}