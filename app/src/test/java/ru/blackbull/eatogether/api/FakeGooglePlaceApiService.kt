package ru.blackbull.eatogether.api

import retrofit2.Response
import ru.blackbull.eatogether.models.googleplaces.OneResult
import ru.blackbull.eatogether.models.googleplaces.ResultList

class FakeGooglePlaceApiService : GooglePlaceApiService {

    override suspend fun getPlaceDetail(
        placeId: String ,
        language: String ,
        apiKey: String ,
        fields: String
    ): OneResult {
        TODO("Not yet implemented")
    }

    override suspend fun getPlacesByName(
        placeName: String ,
        language: String ,
        apiKey: String
    ): ResultList {
        TODO("Not yet implemented")
    }

    override suspend fun getNearbyPlaces(
        location: String ,
        radius: Int ,
        type: String ,
        language: String ,
        apiKey: String
    ): ResultList {
        TODO("Not yet implemented")
    }
}