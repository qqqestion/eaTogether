package ru.blackbull.eatogether.models.googleplaces

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.blackbull.eatogether.models.googleplaces.BasicLocation

@JsonClass(generateAdapter = true)
data class ResultList(
    val status: String ,
    @Json(name = "results")
    val placeList: List<BasicLocation> ,
    @Json(name = "error_message")
    val errorMessage: String = ""
)

