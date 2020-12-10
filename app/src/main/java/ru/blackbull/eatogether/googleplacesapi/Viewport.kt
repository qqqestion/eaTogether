package ru.blackbull.eatogether.googleplacesapi

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Viewport(
    var northeast: Location,
    var southwest: Location
) {}