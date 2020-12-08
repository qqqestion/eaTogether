package ru.blackbull.eatogether.googleplacesapi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceDetail(
//    @Json(name = "business_status")
//    var business_status: String,
//
    @Json(name = "name")
    var name: String,

    @Json(name = "photos")
    var photos: List<Photo>?,

    @Json(name = "opening_hours")
    var opening_hours: OpeningHours?,

    @Json(name = "rating")
    var rating: Double,

    @Json(name = "types")
    var types: List<String>,

    @Json(name = "reviews")
    var reviews: List<Review>?,

    @Json(name = "formatted_address")
    var formatted_address: String,

    @Json(name = "formatted_phone_number")
    var formatted_phone_number: String
) {
    fun getIsOpen(): Boolean? {
        return opening_hours?.open_now
    }
}