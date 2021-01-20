package ru.blackbull.eatogether.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.repository.FirebaseRepository
import ru.blackbull.eatogether.repository.PlaceRepository

class MapViewModel : ViewModel() {

    val searchPlaces: MutableLiveData<List<BasicLocation>> = MutableLiveData()

    val nearbyPlaces: MutableLiveData<List<BasicLocation>> = MutableLiveData()


    private val placeRepository = PlaceRepository()

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