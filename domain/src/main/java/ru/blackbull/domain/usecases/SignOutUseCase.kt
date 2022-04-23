package ru.blackbull.domain.usecases

import ru.blackbull.domain.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke() = authRepository.signOut()
}