package ru.blackbull.domain

import ru.blackbull.domain.models.DomainAuthUser

interface AuthDataSource {

    suspend fun signInWithEmailAndPassword(email: String , password: String)

    suspend fun signUpWithEmailAndPassword(email: String , password: String , authUser: DomainAuthUser)

    fun signOut()
}