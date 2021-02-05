package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceDetail(
    @Json(name = "name")
    var name: String = "" ,

    @Json(name = "photos")
    var photos: List<Photo> = listOf() ,

    @Json(name = "opening_hours")
    var openingHours: OpeningHours? = null ,

    @Json(name = "rating")
    var rating: Double = 0.0 ,

    @Json(name = "reviews")
    var reviews: List<Review> = listOf() ,

    @Json(name = "formatted_address")
    var formatted_address: String = "" ,

    @Json(name = "formatted_phone_number")
    var formatted_phone_number: String = ""
) {
    fun isOpen(): Boolean {
        return openingHours?.isOpenNow ?: false
    }
}