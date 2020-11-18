package com.example.testplacesapi.classesForParsingPlaces

import com.google.gson.annotations.SerializedName

class BasicLocation(
    @SerializedName("business_status") var businessStatus: String,
    var geometry: Geometry,
    var icon: String,
    var name: String,
    var photos: List<Photo>?,
    @SerializedName("place_id") var placeId: String,
    var rating: Double,
    var reference: String,
    var types: List<String>,
    var utc_offset: Int,
    var vicinity: String,
) {}