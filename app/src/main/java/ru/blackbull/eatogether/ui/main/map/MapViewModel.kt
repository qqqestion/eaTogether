package ru.blackbull.eatogether.ui.main.map

import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Geometry
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.blackbull.eatogether.repositories.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val searchResult = placeRepository.searchPlaces

    fun search(query: String , geometry: Geometry) {
        placeRepository.search(query , geometry)
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