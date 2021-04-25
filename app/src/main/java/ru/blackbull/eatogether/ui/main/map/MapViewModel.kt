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
import timber.log.Timber

class MapViewModel @ViewModelInject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _nearbyPlaces: MutableLiveData<Event<Resource<List<BasicLocation>>>> =
        MutableLiveData()
    val nearbyPlaces: LiveData<Event<Resource<List<BasicLocation>>>> = _nearbyPlaces

    fun getNearbyPlaces(lat: Double , lng: Double) = viewModelScope.launch {
        _nearbyPlaces.postValue(Event(Resource.Loading()))
//        val response = placeRepository.getNearbyPlaces("$lat,$lng")
//        _nearbyPlaces.postValue(Event(response))
    }
}