package ru.blackbull.eatogether.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ru.blackbull.eatogether.repositories.PlaceRepository

class MapViewModel @ViewModelInject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {
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