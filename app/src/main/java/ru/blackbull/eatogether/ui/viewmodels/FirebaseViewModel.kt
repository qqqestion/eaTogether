package ru.blackbull.eatogether.ui.viewmodels

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

    val userParties: MutableLiveData<List<Party>> = MutableLiveData()

    val user: MutableLiveData<User> = MutableLiveData()
    val isSignedIn: MutableLiveData<Boolean> = MutableLiveData()

    val signUpResult: MutableLiveData<RegistrationState> = MutableLiveData()

    val selectedParty: MutableLiveData<Party> = MutableLiveData()
    val partyParticipants: MutableLiveData<List<User>> = MutableLiveData()

    fun searchPartyByPlace(partyId: String) = viewModelScope.launch {
        val parties = firebaseRepository.searchPartyByPlace(partyId)
        searchParties.postValue(parties)
    }

    fun addParty(party: Party) = viewModelScope.launch {
        firebaseRepository.addParty(party)
    }

    fun getPartyById(id: String) = viewModelScope.launch {
        val party = firebaseRepository.getPartyById(id)
        selectedParty.postValue(party)
    }

    fun getPartyParticipants(party: Party) = viewModelScope.launch {
        val participants = firebaseRepository.getPartyParticipants(party)
        partyParticipants.postValue(participants)
    }

    fun getPartiesByCurrentUser() = viewModelScope.launch {
        val parties = firebaseRepository.getPartiesByCurrentUser()
        userParties.postValue(parties)
    }

    fun getCurrentUser() = viewModelScope.launch {
        val foundUser = firebaseRepository.getCurrentUser()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        user.postValue(foundUser)
    }

    fun isAuthenticated(): Boolean {
        return firebaseRepository.isAuthenticated()
    }

    fun signUpWithEmailAndPassword(userInfo: User , password: String) = viewModelScope.launch {
//        signUpResult.postValue(RegistrationState.Loading())
//        val response = firebaseRepository.signUpWithEmailAndPassword(userInfo , password)
//        signUpResult.postValue(response)
    }

    fun addUserToParty(party: Party) = viewModelScope.launch {
        firebaseRepository.addCurrentUserToParty(party)
    }
}
