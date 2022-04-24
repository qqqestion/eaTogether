package ru.blackbull.eatogether.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.Party
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.domain.PartyRepository
import ru.blackbull.domain.Resource
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.models.firebase.DomainParty
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.repositories.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class PartyDetailViewModel @Inject constructor(
    private val firebaseRepository: UserRepository,
    private val partyRepository: PartyRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _selectedParty: MutableLiveData<Event<Resource<DomainParty>>> = MutableLiveData()
    val selectedParty: LiveData<Event<Resource<DomainParty>>> = _selectedParty

    private val _partyParticipants: MutableLiveData<Event<Resource<List<DomainUser>>>> = MutableLiveData()
    val partyParticipants: LiveData<Event<Resource<List<DomainUser>>>> = _partyParticipants

    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = placeRepository.placeDetail

    fun getPartyParticipants(party: Party) = viewModelScope.launch {
        _partyParticipants.postValue(Event(Resource.Loading()))
        val response = partyRepository.getPartyParticipants(party.toDomainParty()).toResource()
        _partyParticipants.postValue(Event(response))
    }

    fun updateParty(party: Party) = viewModelScope.launch {
        partyRepository.updateParty(party.toDomainParty())
    }

    fun getPartyById(id: String) = viewModelScope.launch {
        _selectedParty.postValue(Event(Resource.Loading()))
        val response = partyRepository.getPartyById(id).toResource()
        _selectedParty.postValue(Event(response))
    }

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        placeRepository.getPlaceDetail(placeId)
    }

    private val _leavePartyStatus = MutableLiveData<Event<Resource<Unit>>>()
    val leavePartyStatus: LiveData<Event<Resource<Unit>>> = _leavePartyStatus

    fun leaveParty(party: Party) = viewModelScope.launch {
        _leavePartyStatus.postValue(Event(Resource.Loading()))
        val response = partyRepository.leaveParty(party.toDomainParty()).toResource()
        _leavePartyStatus.postValue(Event(response))
    }

    private val _addUserStatus = MutableLiveData<Event<Resource<DomainUser>>>()
    val addUserStatus: LiveData<Event<Resource<DomainUser>>> = _addUserStatus

    fun addUserToParty(party: Party) = viewModelScope.launch {
        partyRepository.addCurrentUserToParty(party.toDomainParty())
        val response = firebaseRepository.getCurrentUser().toResource()
        _addUserStatus.postValue(Event(response))
    }
}