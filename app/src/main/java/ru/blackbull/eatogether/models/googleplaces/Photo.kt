package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class Photo(
    var photo_reference: String,
    var width: Int,
    var height: Int
) {

}