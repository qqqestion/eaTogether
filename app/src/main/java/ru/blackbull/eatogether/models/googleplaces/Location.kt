package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Location(
    var lat: Double,
    var lng: Double
) {
}