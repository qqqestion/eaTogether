package ru.blackbull.domain.usecases

import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.UserAuthValidator
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthDataSource ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<SignInUseCase.Params , Unit>(dispatchers) {

    override suspend fun doWork(params: Params) {
        authRepository.signInWithEmailAndPassword(params.email , params.password)
    }

    data class Params(val email: String , val password: String)
}