package ru.blackbull.eatogether.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.repositories.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class PartyDetailViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository ,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _selectedParty: MutableLiveData<Event<Resource<Party>>> = MutableLiveData()
    val selectedParty: LiveData<Event<Resource<Party>>> = _selectedParty

    private val _partyParticipants: MutableLiveData<Event<Resource<List<User>>>> = MutableLiveData()
    val partyParticipants: LiveData<Event<Resource<List<User>>>> = _partyParticipants

    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = placeRepository.placeDetail

    fun getPartyParticipants(party: Party) = viewModelScope.launch {
        _partyParticipants.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getPartyParticipants(party)
        _partyParticipants.postValue(Event(response))
    }

    fun updateParty(party: Party) = viewModelScope.launch {
        firebaseRepository.updateParty(party)
    }

    fun getPartyById(id: String) = viewModelScope.launch {
        _selectedParty.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getPartyById(id)
        _selectedParty.postValue(Event(response))
    }

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        placeRepository.getPlaceDetail(placeId)
    }

    private val _leavePartyStatus = MutableLiveData<Event<Resource<Unit>>>()
    val leavePartyStatus: LiveData<Event<Resource<Unit>>> = _leavePartyStatus

    fun leaveParty(party: Party) = viewModelScope.launch {
        _leavePartyStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.leaveParty(party)
        _leavePartyStatus.postValue(Event(response))
    }

    private val _addUserStatus = MutableLiveData<Event<Resource<User>>>()
    val addUserStatus: LiveData<Event<Resource<User>>> = _addUserStatus

    fun addUserToParty(party: Party) = viewModelScope.launch {
        firebaseRepository.addCurrentUserToParty(party)
        val response = firebaseRepository.getCurrentUser()
        _addUserStatus.postValue(Event(response))
    }
}