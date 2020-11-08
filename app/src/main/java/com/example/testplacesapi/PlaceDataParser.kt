package com.example.testplacesapi

import com.example.testplacesapi.classesForParsing.Basic
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.net.URL
import kotlin.concurrent.thread

class PlaceDataParser {
    private val apiKey = "AIzaSyDCvP07ssvmpkykrJ3QN5_BzGifwa4Weqo"
    fun getPlacesData(latLng: LatLng) : String{
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                latLng.latitude + "," +
                latLng.longitude + "&radius=1000&type=restaurant" +
                "&key=" + apiKey

        return downloadURL(url)
    }

    fun getPhotoUrl(photoReference: String, width: Int, height: Int): String{
        val url = "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=" + width +
                "&maxheight=" + height +
                "&photoreference=" + photoReference +
                "&key=" + apiKey
        return downloadURL(url)
    }

    fun getGSONData(jsonString: String ) : Basic{
        return Gson().fromJson(jsonString, Basic::class.java)
    }

    fun execute(latLng: LatLng): Basic{
        return getGSONData(getPlacesData(latLng))
    }

     private fun downloadURL(string: String) : String{
        return URL(string).readText()
    }

}