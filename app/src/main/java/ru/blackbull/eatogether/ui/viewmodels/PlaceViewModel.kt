package ru.blackbull.eatogether.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.type.LatLng
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.models.googleplaces.Location
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.repository.PlaceRepository

class PlaceViewModel : ViewModel() {

    private val placeRepository = PlaceRepository()

    val placeDetail: MutableLiveData<PlaceDetail> = MutableLiveData()

    val searchPlaces: MutableLiveData<List<BasicLocation>> = MutableLiveData()

    val nearbyPlaces: MutableLiveData<List<BasicLocation>> = MutableLiveData()

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        val response = placeRepository.getPlaceDetail(placeId)
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.status == "OK") {
                    placeDetail.postValue(it.placeDetail)
                }
            }
        }
    }


}