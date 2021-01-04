package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class OneResult(
    val status: String ,

    @Json(name = "result")
    val placeDetail: PlaceDetail ,

    @Json(name = "error_message")
    val errorMessage: String = ""
) {
}
