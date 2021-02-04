package ru.blackbull.eatogether.repositories

import ru.blackbull.eatogether.api.GooglePlaceApiService


class PlaceRepository(
    private val googlePlaceApiService: GooglePlaceApiService
) {
    suspend fun getPlaceDetail(
        placeId: String
    ) = googlePlaceApiService.getPlaceDetail(placeId)

    suspend fun getPlacesByName(
        placeName: String
    ) = googlePlaceApiService.getPlacesByName(placeName)

    suspend fun getNearbyPlaces(
        location: String
    ) = googlePlaceApiService.getNearbyPlaces(location)
}
