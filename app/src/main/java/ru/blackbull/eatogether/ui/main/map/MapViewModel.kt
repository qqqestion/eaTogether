package ru.blackbull.eatogether.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.PlaceRepository

class MapViewModel @ViewModelInject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _searchPlaces: MutableLiveData<Event<Resource<List<BasicLocation>>>> =
        MutableLiveData()
    val searchPlaces: LiveData<Event<Resource<List<BasicLocation>>>> = _searchPlaces

    private val _nearbyPlaces: MutableLiveData<Event<Resource<List<BasicLocation>>>> =
        MutableLiveData()
    val nearbyPlaces: LiveData<Event<Resource<List<BasicLocation>>>> = _nearbyPlaces


    fun searchPlaces(placeName: String) = viewModelScope.launch {
        _searchPlaces.postValue(Event(Resource.Loading()))
        val response = placeRepository.getPlacesByName(placeName)
        _searchPlaces.postValue(Event(response))
    }

    fun getNearbyPlaces(lat: Double , lng: Double) = viewModelScope.launch {
        _nearbyPlaces.postValue(Event(Resource.Loading()))
        val response = placeRepository.getNearbyPlaces("$lat,$lng")
        _nearbyPlaces.postValue(Event(response))
    }
}