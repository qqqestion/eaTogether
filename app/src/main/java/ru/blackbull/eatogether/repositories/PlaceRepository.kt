package ru.blackbull.eatogether.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import timber.log.Timber
import javax.inject.Inject


class PlaceRepository @Inject constructor(
    private val searchManager: SearchManager,
): Session.SearchListener {
//
//    suspend fun getPlaceDetail(
//        placeId: String
//    ): Resource<PlaceDetail> = withContext(Dispatchers.IO) {
//        val response = googlePlaceApiService.getPlaceDetail(placeId)
//        return@withContext if (response.status == "OK") {
//            Resource.Success(response.placeDetail)
//        } else {
//            Resource.Error(msg = response.errorMessage)
//        }
//    }
//
//    suspend fun getPlacesByName(
//        placeName: String
//    ): Resource<List<BasicLocation>> = withContext(Dispatchers.IO) {
//        val response = googlePlaceApiService.getPlacesByName(placeName)
//        return@withContext if (response.status == "OK") {
//            Resource.Success(response.placeList)
//        } else {
//            Resource.Error(msg = response.errorMessage)
//        }
//    }
//
//    suspend fun getNearbyPlaces(
//        location: String
//    ): Resource<List<BasicLocation>> = withContext(Dispatchers.IO) {
//        val response = googlePlaceApiService.getNearbyPlaces(location)
//        return@withContext if (response.status == "OK") {
//            Resource.Success(response.placeList)
//        } else {
//            Resource.Error(msg = response.errorMessage)
//        }
//    }

    private val _placeDetail: MutableLiveData<Event<Resource<PlaceDetail>>> = MutableLiveData()
    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = _placeDetail

    private var searchSession: Session? = null

    fun getPlaceDetail(placeId: String) {
        _placeDetail.postValue(Event(Resource.Loading()))
        searchSession = searchManager.resolveURI(
            placeId ,
            SearchOptions().apply {
                snippets = Snippet.BUSINESS_RATING1X.value
            } ,
            this
        )
    }

    override fun onSearchResponse(response: Response) {
        for (searchResult in response.collection.children) {
            val obj = searchResult.obj!!
            val businessMetadata = obj.metadataContainer.getItem(BusinessObjectMetadata::class.java)
            val ratingMetadata =
                obj.metadataContainer.getItem(BusinessRating1xObjectMetadata::class.java)
            val id = businessMetadata.oid
            val score = ratingMetadata?.score
            val ratings = ratingMetadata?.ratings
            val name = businessMetadata.name
            val address = businessMetadata.address.formattedAddress
            val phones = businessMetadata.phones.map { it.formattedNumber }
            val workingHours = businessMetadata.workingHours?.state?.text
            val categories = businessMetadata.categories.map { it.name }
            val features = businessMetadata.features.map { it.id to it.value }
            _placeDetail.postValue(
                Event(
                    Resource.Success(
                        PlaceDetail(
                            id ,
                            name ,
                            address ,
                            phones.firstOrNull() ,
                            workingHours ,
                            score ,
                            ratings ,
                            categories
//                            features
                        )
                    )
                )
            )
        }
    }

    override fun onSearchError(error: Error) {
        Timber.d("Yandex search error: $error")
        val errorMessage = when (error) {
            is RemoteError -> "Remote error"
            is NetworkError -> "Network error"
            else -> "Unknown error"
        }
        _placeDetail.postValue(Event(Resource.Error(null , errorMessage)))
    }
}
