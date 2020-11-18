package com.example.testplacesapi

import com.example.testplacesapi.classesForParsingPlaces.ResultSet
import com.example.testplacesapi.classesForParsingPlaceDetails.BasicDetails
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.net.URL
import java.net.URLEncoder

class PlaceDataParser {
    private val apiKey = "AIzaSyDCvP07ssvmpkykrJ3QN5_BzGifwa4Weqo"

    companion object {
        private val baseUrl = "https://maps.googleapis.com/maps/api/"
        private val format = "json"
    }

    fun getPlacesData(latLng: LatLng): String {
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "language=ru" +
                    "&location=" + latLng.latitude + "," + latLng.longitude +
                    "&radius=1000" +
                    "&type=restaurant" +
                    "&key=" + apiKey
        return downloadURL(url)
    }

    suspend fun getGSONPlaceDetails(placeID: String): BasicDetails {
        val fields = arrayListOf<String>(
            "name", "rating", "formatted_phone_number",
            "review", "photos", "formatted_address", "opening_hours"
        )
        val language = "ru"
        val url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "place_id=$placeID" +
                "&language=${language}" +
                "&fields=${fields.joinToString(",")}" +
                "&key=$apiKey"
        val string = asyncDownloadURL(url)
        return Gson().fromJson(string, BasicDetails::class.java)
    }

    suspend fun getPlaceByName(query: String): ResultSet? {
        val fields = arrayListOf<String>(
            "name", "geometry", "place_id",
        )
        val language = "ru"
        val url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "language=${language}" +
                "&input=${URLEncoder.encode(
                    query,
                    "utf-8"
                )}" + // TODO: спросить что за предупреждение
                "&inputtype=textquery" +
                "&fields=${fields.joinToString(",")}" +
                "&key=$apiKey"
        val string = asyncDownloadURL(url)
        return Gson().fromJson(string, ResultSet::class.java)
    }

    fun getPhotoUrl(photoReference: String, width: Int, height: Int): String {
        val url = "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=" + width +
                "&maxheight=" + height +
                "&photoreference=" + photoReference +
                "&key=" + apiKey
        return url
    }

    fun getGSONData(jsonString: String): ResultSet {
        return Gson().fromJson(jsonString, ResultSet::class.java)
    }

    fun execute(latLng: LatLng): ResultSet {
        return getGSONData(getPlacesData(latLng))
    }

    private fun downloadURL(string: String): String {
        return URL(string).readText()
    }

    suspend fun asyncDownloadURL(string: String): String {
        return withContext(Dispatchers.IO) {
            return@withContext URL(string).readText()
        }
    }

}