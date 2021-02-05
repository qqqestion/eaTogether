package ru.blackbull.eatogether.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.googleplaces.OneResult
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.repositories.PlaceRepository

class PlaceDetailViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository ,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _placeDetail: MutableLiveData<Event<Resource<PlaceDetail>>> = MutableLiveData()
    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = _placeDetail

    private val _searchParties: MutableLiveData<Event<Resource<List<Party>>>> = MutableLiveData()
    val searchParties: LiveData<Event<Resource<List<Party>>>> = _searchParties

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        _placeDetail.postValue(Event(Resource.Loading()))
        val response = placeRepository.getPlaceDetail(placeId)
        _placeDetail.postValue(Event(response))
    }

    fun searchPartyByPlace(partyId: String) = viewModelScope.launch {
        _searchParties.postValue(Event(Resource.Loading()))
        val parties = firebaseRepository.searchPartyByPlace(partyId)
        _searchParties.postValue(Event(parties))
    }

    fun addUserToParty(party: Party) = viewModelScope.launch {
        firebaseRepository.addCurrentUserToParty(party)
    }
}