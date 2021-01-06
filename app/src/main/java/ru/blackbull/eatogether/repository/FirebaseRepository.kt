package ru.blackbull.eatogether.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.api.NetworkModule
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.state.RegistrationState
import java.lang.RuntimeException


class FirebaseRepository {

    suspend fun searchPartyByPlace(placeId: String) =
        NetworkModule.firebaseApiService.searchPartyByPlace(placeId)

    fun addParty(party: Party) =
        NetworkModule.firebaseApiService.addParty(party)

    suspend fun getPartiesByCurrentUser() =
        NetworkModule.firebaseApiService.getPartiesByCurrentUser()


    suspend fun getCurrentUser() =
        NetworkModule.firebaseApiService.getCurrentUser()

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun updateUser(user: User) {
        NetworkModule.firebaseApiService.updateUser(user)
    }

    fun isAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    suspend fun getCurrentUserPhotoUri(): Uri {
        return NetworkModule.firebaseApiService.getCurrentUserPhotoUri()
    }

    suspend fun signIn(email: String , password: String): Boolean {
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

    suspend fun signUpWithEmailAndPassword(
        userInfo: User ,
        password: String
    ): RegistrationState {
        val firebaseUser: FirebaseUser?
        try {
            val result = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(userInfo.email!! , password)
                .await()
            firebaseUser = result.user
        } catch (e: FirebaseException) {
            Log.d("RegistrationDebug" , "an error occurred" , e)
            return RegistrationState.Error(e)
        }
        NetworkModule.firebaseApiService.addUser(firebaseUser?.uid!! , userInfo)
        return RegistrationState.Success()
    }
}
