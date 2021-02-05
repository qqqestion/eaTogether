package ru.blackbull.eatogether.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.repositories.PlaceRepository

class PartyDetailViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository ,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _selectedParty: MutableLiveData<Event<Resource<Party>>> = MutableLiveData()
    val selectedParty: LiveData<Event<Resource<Party>>> = _selectedParty

    private val _partyParticipants: MutableLiveData<Event<Resource<List<User>>>> = MutableLiveData()
    val partyParticipants: LiveData<Event<Resource<List<User>>>> = _partyParticipants

    private val _placeDetail: MutableLiveData<Event<Resource<PlaceDetail>>> = MutableLiveData()
    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = _placeDetail

    fun getPartyParticipants(party: Party) = viewModelScope.launch {
        _partyParticipants.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getPartyParticipants(party)
        _partyParticipants.postValue(Event(response))
    }

    fun getPartyById(id: String) = viewModelScope.launch {
        _selectedParty.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getPartyById(id)
        _selectedParty.postValue(Event(response))
    }

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        _placeDetail.postValue(Event(Resource.Loading()))
        val response = placeRepository.getPlaceDetail(placeId)
        _placeDetail.postValue(Event(response))
    }
}