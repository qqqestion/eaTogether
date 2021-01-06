package ru.blackbull.eatogether.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.repository.FirebaseRepository
import ru.blackbull.eatogether.state.RegistrationState


class FirebaseViewModel : ViewModel() {
    private val firebaseRepository = FirebaseRepository()

    val searchParties: MutableLiveData<List<Party>> = MutableLiveData()

    val user: MutableLiveData<User> = MutableLiveData()
    val isSignedIn: MutableLiveData<Boolean> = MutableLiveData()

    val signUpResult: MutableLiveData<RegistrationState> = MutableLiveData()

    val currentUserPhoto: MutableLiveData<Uri> = MutableLiveData()

    fun searchPartyByPlace(partyId: String) = viewModelScope.launch {
        val parties = firebaseRepository.searchPartyByPlace(partyId)
        searchParties.postValue(parties)
    }

    fun addParty(party: Party) = viewModelScope.launch {
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

    fun updateUser(updatedUser: User) = viewModelScope.launch {
        updatedUser.imageUri = user.value?.imageUri
        firebaseRepository.updateUser(updatedUser)
        user.postValue(updatedUser)
    }

    fun isAuthenticated(): Boolean {
        return firebaseRepository.isAuthenticated()
    }

    fun getCurrentUserPhotoUri() = viewModelScope.launch {
        val uri = firebaseRepository.getCurrentUserPhotoUri()
        Log.d("ImageDebug" , "savedUri in getCurrentUserPhotoUri: $uri")
        currentUserPhoto.postValue(uri)
    }

    fun signIn(email: String , password: String) = viewModelScope.launch {
        val isSignedInLocal = firebaseRepository.signIn(email , password)
        isSignedIn.postValue(isSignedInLocal)
    }

    fun signUpWithEmailAndPassword(userInfo: User , password: String) = viewModelScope.launch {
        signUpResult.postValue(RegistrationState.Loading())
        val response = firebaseRepository.signUpWithEmailAndPassword(userInfo , password)
        signUpResult.postValue(response)
    }
}
