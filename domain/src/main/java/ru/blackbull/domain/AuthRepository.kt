package ru.blackbull.domain

import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.DomainAuthUser
import ru.blackbull.domain.usecases.NetworkError
import ru.blackbull.domain.usecases.SignInError
import ru.blackbull.domain.usecases.SignUpError

interface AuthRepository {

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Either<SignInError, Unit>

    suspend fun createAccount(email: String, password: String): Either<SignUpError, Unit>

    suspend fun completeRegistration(user: DomainAuthUser): Either<NetworkError, Unit>

    suspend fun checkAuthenticated(): Either<NetworkError, Boolean>

    suspend fun isAccountInfoSet(): Either<NetworkError, Boolean>

    fun signOut(): Either<NetworkError, Unit>
}

