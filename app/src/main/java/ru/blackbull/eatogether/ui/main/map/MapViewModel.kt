package ru.blackbull.eatogether.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Geometry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.PartyWithUser
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.eatogether.other.Event
import ru.blackbull.domain.Resource
import ru.blackbull.data.models.firebase.toPartyWithUser
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.functional.Either
import ru.blackbull.eatogether.repositories.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val partyRepository: PartyDataSource ,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val searchResult = placeRepository.searchPlaces

    fun search(query: String , geometry: Geometry) {
        placeRepository.search(query , geometry)
    }

    val cuisine = placeRepository.cuisine

    fun getCuisineList() {
        placeRepository.getCuisineList()
    }

    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = placeRepository.placeDetail

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        placeRepository.getPlaceDetail(placeId)
    }

    private val _searchParties: MutableLiveData<Event<Resource<List<PartyWithUser>>>> =
        MutableLiveData()
    val searchParties: LiveData<Event<Resource<List<PartyWithUser>>>> = _searchParties

    fun searchPartyByPlace(placeId: String) = viewModelScope.launch {
        _searchParties.postValue(Event(Resource.Loading()))
        val parties = (partyRepository.searchPartyByPlace(placeId) as Either.Right).value.map { it.toPartyWithUser() }
        _searchParties.postValue(Event(Resource.Success(parties)))
    }

    fun addUserToParty(party: PartyWithUser) = viewModelScope.launch {
        partyRepository.addCurrentUserToParty(party.toParty().toDomainParty())
    }

//    private val _nearbyPlaces: MutableLiveData<Event<Resource<List<BasicLocation>>>> =
//        MutableLiveData()
//    val nearbyPlaces: LiveData<Event<Resource<List<BasicLocation>>>> = _nearbyPlaces

//    fun getNearbyPlaces(lat: Double , lng: Double) = viewModelScope.launch {
//        _nearbyPlaces.postValue(Event(Resource.Loading()))
//        val response = placeRepository.getNearbyPlaces("$lat,$lng")
//        _nearbyPlaces.postValue(Event(response))
//    }
}