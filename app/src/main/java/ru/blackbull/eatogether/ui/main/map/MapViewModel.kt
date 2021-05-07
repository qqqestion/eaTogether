package ru.blackbull.eatogether.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Geometry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.repositories.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository ,
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

    private val _searchParties: MutableLiveData<Event<Resource<List<Party>>>> = MutableLiveData()
    val searchParties: LiveData<Event<Resource<List<Party>>>> = _searchParties

    fun searchPartyByPlace(placeId: String) = viewModelScope.launch {
        _searchParties.postValue(Event(Resource.Loading()))
        val parties = firebaseRepository.searchPartyByPlace(placeId)
        _searchParties.postValue(Event(parties))
    }

    fun addUserToParty(party: Party) = viewModelScope.launch {
        firebaseRepository.addCurrentUserToParty(party)
    }

//
//    private val _nearbyPlaces: MutableLiveData<Event<Resource<List<BasicLocation>>>> =
//        MutableLiveData()
//    val nearbyPlaces: LiveData<Event<Resource<List<BasicLocation>>>> = _nearbyPlaces

//    fun getNearbyPlaces(lat: Double , lng: Double) = viewModelScope.launch {
//        _nearbyPlaces.postValue(Event(Resource.Loading()))
//        val response = placeRepository.getNearbyPlaces("$lat,$lng")
//        _nearbyPlaces.postValue(Event(response))
//    }
}