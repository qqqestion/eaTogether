package ru.blackbull.eatogether.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.api.NetworkModule
import ru.blackbull.eatogether.models.firebase.NewParty
import ru.blackbull.eatogether.models.firebase.NewUser
import java.lang.RuntimeException


class FirebaseRepository {

    suspend fun searchPartyByPlace(placeId: String) =
        NetworkModule.firebaseApiService.searchPartyByPlace(placeId)

    fun addParty(party: NewParty) =
        NetworkModule.firebaseApiService.addParty(party)

    suspend fun getCurrentUser() =
        NetworkModule.firebaseApiService.getCurrentUser()

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun updateUser(user: NewUser) {
        NetworkModule.firebaseApiService.updateUser(user)
    }

    fun isAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    suspend fun getCurrentUserPhotoUri(): Uri {
        return NetworkModule.firebaseApiService.getCurrentUserPhotoUri()
    }

    suspend fun signIn(email: String , password: String): Boolean? {
        val firebaseUser: FirebaseUser?
        try {
            val result = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email , password)
                .await()
            firebaseUser = result.user
        } catch (e: FirebaseException) {
            when (e) {
                is FirebaseAuthInvalidUserException ,
                is FirebaseAuthInvalidCredentialsException -> {
                    return false
                }
                else -> {
                    throw RuntimeException(e)
                }
            }
        }
        return firebaseUser != null
    }
}
