package ru.blackbull.domain.usecases

import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.firebase.DomainUser
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(user: DomainUser): Either<UpdateUserError, DomainUser>
        = userRepository.updateUser(user)
}

sealed interface UpdateUserError
