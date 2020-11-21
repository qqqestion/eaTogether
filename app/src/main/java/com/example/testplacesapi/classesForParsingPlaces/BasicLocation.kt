package com.example.testplacesapi.classesForParsingPlaces

import com.google.gson.annotations.SerializedName

data class BasicLocation(
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
) {
    override fun toString(): String {
        return "BasicLocation(businessStatus='$businessStatus', geometry=$geometry, icon='$icon', name='$name', photos=$photos, placeId='$placeId', rating=$rating, reference='$reference', types=$types, utc_offset=$utc_offset, vicinity='$vicinity')"
    }
}