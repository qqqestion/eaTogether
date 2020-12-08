package ru.blackbull.eatogether.googleplacesapi

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class OpeningHours(
    var open_now: Boolean,
) {
}
