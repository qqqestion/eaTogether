package ru.blackbull.eatogether.googleplacesapi

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Location(
    var lat: Double,
    var lng: Double
) {
}