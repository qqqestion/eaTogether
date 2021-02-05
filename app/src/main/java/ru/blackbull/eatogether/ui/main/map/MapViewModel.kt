package ru.blackbull.eatogether.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.repositories.PlaceRepository

class MapViewModel @ViewModelInject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val searchPlaces: MutableLiveData<List<BasicLocation>> = MutableLiveData()

    val nearbyPlaces: MutableLiveData<List<BasicLocation>> = MutableLiveData()


    fun searchPlaces(placeName: String) = viewModelScope.launch {
        val response = placeRepository.getPlacesByName(placeName)
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.status == "OK") {
                    searchPlaces.postValue(it.placeList)
                }
            }
        }
    }

    fun getNearbyPlaces(lat: Double , lng: Double) = viewModelScope.launch {
        val response = placeRepository.getNearbyPlaces("$lat,$lng")
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.status == "OK") {
                    nearbyPlaces.postValue(it.placeList)
                }
            }
        }
    }
}