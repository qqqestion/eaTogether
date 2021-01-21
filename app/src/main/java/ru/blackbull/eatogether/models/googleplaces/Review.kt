package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "author_name")
    var authorName: String ,
    @Json(name = "author_url")
    var authorUrl: String ,
    var language: String ,
    @Json(name = "profile_photo_url")
    var profilePhotoUrl: String ,
    var rating: Int ,
    @Json(name = "relative_time_description")
    var relativeTimeDescription: String ,
    var text: String ,
    var time: Int ,
)
