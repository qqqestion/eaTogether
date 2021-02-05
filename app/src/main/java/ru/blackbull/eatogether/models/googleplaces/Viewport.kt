package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.JsonClass
import ru.blackbull.eatogether.models.googleplaces.Location

@JsonClass(generateAdapter = true)
class Viewport(
    var northeast: Location ,
    var southwest: Location
) {}