package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class OpeningHours(
    @Json(name = "open_now")
    var isOpenNow: Boolean
) {
}
