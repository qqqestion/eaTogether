package ru.blackbull.eatogether.repository

import ru.blackbull.eatogether.api.NetworkModule


class PlaceRepository {
    suspend fun getPlaceDetail(
        placeId: String
    ) = NetworkModule.googlePlaceApiService.getPlaceDetail(placeId)

    suspend fun getPlacesByName(
        placeName: String
    ) = NetworkModule.googlePlaceApiService.getPlacesByName(placeName)

    suspend fun getNearbyPlaces(
        location: String
    ) = NetworkModule.googlePlaceApiService.getNearbyPlaces(location)
}
