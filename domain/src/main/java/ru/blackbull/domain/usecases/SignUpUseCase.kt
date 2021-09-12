package ru.blackbull.domain.usecases

import ru.blackbull.domain.*
import ru.blackbull.domain.exceptions.ConfirmPasswordException
import ru.blackbull.domain.exceptions.PasswordValidationException
import ru.blackbull.domain.models.DomainAuthUser
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthDataSource ,
    private val userAuthValidator: UserAuthValidator ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<SignUpUseCase.Params , Unit>(dispatchers) {

    override suspend fun doWork(params: Params) {
        userAuthValidator.validateEmail(params.email)
        userAuthValidator.validatePasswordAndConfirmedPassword(
            params.password ,
            params.confirmedPassword
        )
        authRepository.signUpWithEmailAndPassword(params.email , params.password , params.authUser)
    }

    data class Params(
        val email: String ,
        val password: String ,
        val confirmedPassword: String ,
        val authUser: DomainAuthUser
    )
}