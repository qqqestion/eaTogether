package ru.blackbull.domain.usecases

import ru.blackbull.domain.*
import ru.blackbull.domain.models.DomainAuthUser
import javax.inject.Inject

class SetAccountInfoUseCase @Inject constructor(
    private val authRepository: AuthDataSource ,
    private val userDataValidator: UserDataValidator ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<SetAccountInfoUseCase.Params , Unit>(dispatchers) {

    /*
        FirebaseAuthWeakPasswordException thrown if the password is not strong enough
        FirebaseAuthInvalidCredentialsException thrown if the email address is malformed
        FirebaseAuthUserCollisionException thrown if there already exists an account with the given email address
    */

    override suspend fun doWork(params: Params) {
        userDataValidator.validateFirstName(params.user.firstName)
        userDataValidator.validateLastName(params.user.lastName)
        userDataValidator.validateDescription(params.user.description)
        userDataValidator.validateBirthday(params.user.birthday)
        authRepository.setAccountInfo(params.user)
    }

    data class Params(
        val user: DomainAuthUser
    )
}