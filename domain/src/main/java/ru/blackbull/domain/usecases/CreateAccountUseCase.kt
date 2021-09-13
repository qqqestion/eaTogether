package ru.blackbull.domain.usecases

import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.UserAuthValidator
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authRepository: AuthDataSource ,
    private val userAuthValidator: UserAuthValidator ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<CreateAccountUseCase.Params , Unit>(dispatchers) {

    override suspend fun doWork(params: Params) {
        userAuthValidator.validateEmail(params.email)
        userAuthValidator.validatePasswordAndConfirmedPassword(
            params.password ,
            params.confirmedPassword
        )
        authRepository.createAccount(params.email , params.password)
    }

    data class Params(
        val email: String ,
        val password: String ,
        val confirmedPassword: String
    )
}