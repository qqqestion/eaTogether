package ru.blackbull.domain

import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.DomainAuthUser
import ru.blackbull.domain.usecases.NetworkError
import ru.blackbull.domain.usecases.SignInError
import ru.blackbull.domain.usecases.SignUpError

interface AuthDataSource {

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Either<SignInError, Unit>

    suspend fun createAccount(email: String, password: String): Either<SignUpError, Unit>

    suspend fun setAccountInfo(user: DomainAuthUser)

    suspend fun checkAuthenticated(): Either<NetworkError, Boolean>

    suspend fun isAccountInfoSet(): Either<NetworkError, Boolean>

    fun signOut()
}

