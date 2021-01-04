package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Geometry(
    var location: Location ,
    var viewport: Viewport
) {
}