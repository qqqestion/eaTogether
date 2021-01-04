package ru.blackbull.eatogether.repository

import android.net.Uri
import ru.blackbull.eatogether.api.NetworkModule
import ru.blackbull.eatogether.models.firebase.NewParty
import ru.blackbull.eatogether.models.firebase.NewUser


class FirebaseRepository {

    suspend fun searchPartyByPlace(placeId: String) =
        NetworkModule.firebaseApiService.searchPartyByPlace(placeId)

    fun addParty(party: NewParty) =
        NetworkModule.firebaseApiService.addParty(party)

    suspend fun getCurrentUser() =
        NetworkModule.firebaseApiService.getCurrentUser()

    fun signOut() {
        NetworkModule.firebaseApiService.signOut()
    }

    suspend fun updateUser(user: NewUser) {
        NetworkModule.firebaseApiService.updateUser(user)
    }

    fun isAuthenticated(): Boolean {
        return NetworkModule.firebaseApiService.isAuthenticated()
    }

    suspend fun getCurrentUserPhotoUri(): Uri {
        return NetworkModule.firebaseApiService.getCurrentUserPhotoUri()
    }
}
