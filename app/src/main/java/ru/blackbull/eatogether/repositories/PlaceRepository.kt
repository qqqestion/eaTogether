package ru.blackbull.eatogether.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.blackbull.eatogether.api.GooglePlaceApiService
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.other.Resource


class PlaceRepository(
    private val googlePlaceApiService: GooglePlaceApiService
) {
    suspend fun getPlaceDetail(
        placeId: String
    ): Resource<PlaceDetail> = withContext(Dispatchers.IO) {
        val response = googlePlaceApiService.getPlaceDetail(placeId)
        if (response.isSuccessful) {
            response.body()?.let {
                return@withContext if (it.status == "OK") {
                    Resource.Success(it.placeDetail)
                } else {
                    Resource.Error<PlaceDetail>(msg = it.errorMessage)
                }
            }
        }
        return@withContext Resource.Error<PlaceDetail>()
    }

    suspend fun getPlacesByName(
        placeName: String
    ): Resource<List<BasicLocation>> = withContext(Dispatchers.IO) {
        val response = googlePlaceApiService.getPlacesByName(placeName)
        if (response.isSuccessful) {
            response.body()?.let { resultList ->
                return@withContext if (resultList.status == "OK") {
                    Resource.Success(resultList.placeList)
                } else {
                    Resource.Error<List<BasicLocation>>(msg = resultList.errorMessage)
                }
            }
        }
        return@withContext Resource.Error<List<BasicLocation>>()
    }

    suspend fun getNearbyPlaces(
        location: String
    ): Resource<List<BasicLocation>> = withContext(Dispatchers.IO) {
        val response = googlePlaceApiService.getNearbyPlaces(location)
        if (response.isSuccessful) {
            response.body()?.let { resultList ->
                return@withContext if (resultList.status == "OK") {
                    Resource.Success(resultList.placeList)
                } else {
                    Resource.Error<List<BasicLocation>>(msg = resultList.errorMessage)
                }
            }
        }
        return@withContext Resource.Error<List<BasicLocation>>()
    }
}
