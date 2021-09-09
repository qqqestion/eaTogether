package ru.blackbull.eatogether.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.data.models.firebase.Party
import ru.blackbull.eatogether.other.Event
import ru.blackbull.domain.Resource
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import ru.blackbull.eatogether.repositories.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val partyRepository: PartyDataSource ,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = placeRepository.placeDetail

    private val _searchParties: MutableLiveData<Event<Resource<List<DomainPartyWithUser>>>> =
        MutableLiveData()
    val searchParties: LiveData<Event<Resource<List<DomainPartyWithUser>>>> = _searchParties

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        placeRepository.getPlaceDetail(placeId)
    }

    fun searchPartyByPlace(placeId: String) = viewModelScope.launch {
        _searchParties.postValue(Event(Resource.Loading()))
        val parties = partyRepository.searchPartyByPlace(placeId).toResource()
        _searchParties.postValue(Event(parties))
    }

    fun addUserToParty(party: Party) = viewModelScope.launch {
        partyRepository.addCurrentUserToParty(party.toDomainParty())
    }
}