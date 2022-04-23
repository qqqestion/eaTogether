package ru.blackbull.domain.usecases

import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.firebase.DomainUser
import javax.inject.Inject

open class GetUserUseCase @Inject constructor(
    protected val userRepository: UserRepository
) {

    suspend operator fun invoke(id: String? = null): Either<GetUserError, DomainUser?> =
        when (id) {
            null -> userRepository.getCurrentUser()
            else -> userRepository.getUser(id)
        }
}