package ru.blackbull.eatogether.googleplacesapi

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BasicLocation(
    @Json(name = "business_status")
    var businessStatus: String? ,
    @Json(name = "place_id")
    var placeId: String ,

    var geometry: Geometry ,
    var icon: String ,
    var name: String ,
    var photos: List<Photo> = listOf() ,
    var rating: Double = 0.0 ,
    var reference: String ,
    var types: List<String> = listOf(),
//    var utc_offset: Int ,
    var vicinity: String = ""
) {
//    override fun toString(): String {
//        return "BasicLocation(businessStatus='$businessStatus', geometry=$geometry, icon='$icon', name='$name', photos=$photos, placeId='$placeId', rating=$rating, reference='$reference', types=$types, utc_offset=$utc_offset, vicinity='$vicinity')"
//    }
}