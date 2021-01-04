package ru.blackbull.eatogether.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.NewParty
import ru.blackbull.eatogether.models.firebase.NewUser
import ru.blackbull.eatogether.repository.FirebaseRepository


class FirebaseViewModel : ViewModel() {
    private val firebaseRepository = FirebaseRepository()

    val searchParties: MutableLiveData<List<NewParty>> = MutableLiveData()

    val user: MutableLiveData<NewUser> = MutableLiveData()
    val currentUserPhoto: MutableLiveData<Uri> = MutableLiveData()

    fun searchPartyByPlace(partyId: String) = viewModelScope.launch {
        val parties = firebaseRepository.searchPartyByPlace(partyId)
        searchParties.postValue(parties)
    }

    fun addParty(party: NewParty) = viewModelScope.launch {
        firebaseRepository.addParty(party)
    }

    fun getCurrentUser() = viewModelScope.launch {
        val foundUser = firebaseRepository.getCurrentUser()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        user.postValue(foundUser)
    }

    fun signOut() = viewModelScope.launch {
        firebaseRepository.signOut()
    }

    fun updateUser(user: NewUser) = viewModelScope.launch{
        firebaseRepository.updateUser(user)
    }

    fun isAuthenticated(): Boolean {
        return firebaseRepository.isAuthenticated()
    }

    fun getCurrentUserPhotoUri() = viewModelScope.launch {
        val uri = firebaseRepository.getCurrentUserPhotoUri()
        currentUserPhoto.postValue(uri)
    }
}
