package ru.blackbull.domain

import ru.blackbull.domain.models.DomainAuthUser

interface AuthDataSource {

    suspend fun signInWithEmailAndPassword(email: String , password: String)

    suspend fun createAccount(email: String , password: String)

    suspend fun setAccountInfo(user: DomainAuthUser)

    suspend fun isAccountInfoSet(): Boolean

    fun signOut()
}