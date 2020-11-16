package com.example.testplacesapi

import com.example.testplacesapi.classesForParsingPlaces.Basic
import com.example.testplacesapi.classesForParsingPlaceDetails.BasicDetails
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.net.URL

class PlaceDataParser {
    private val apiKey = "AIzaSyDCvP07ssvmpkykrJ3QN5_BzGifwa4Weqo"
    fun getPlacesData(latLng: LatLng): String {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                latLng.latitude + "," +
                latLng.longitude + "&radius=1000&type=restaurant" +
                "&key=" + apiKey

        return downloadURL(url)
    }

    suspend fun getGSONPlaceDetails(placeID: String): BasicDetails {
        val url =
            " https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeID" +
                    "&fields=name,rating,formatted_phone_number,reviews,photos,formatted_address,opening_hours" +
                    "&key=$apiKey"
        val string = asyncDownloadURL(url)
        return Gson().fromJson(string, BasicDetails::class.java)
    }

    fun getPhotoUrl(photoReference: String, width: Int, height: Int): String {
        val url = "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=" + width +
                "&maxheight=" + height +
                "&photoreference=" + photoReference +
                "&key=" + apiKey
        return url
    }

    fun getGSONData(jsonString: String): Basic {
        return Gson().fromJson(jsonString, Basic::class.java)
    }

    fun execute(latLng: LatLng): Basic {
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