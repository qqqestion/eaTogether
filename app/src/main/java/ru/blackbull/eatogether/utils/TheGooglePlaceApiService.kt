package ru.blackbull.eatogether.utils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.blackbull.eatogether.googleplacesapi.BasicLocation
import ru.blackbull.eatogether.googleplacesapi.OneResult
import ru.blackbull.eatogether.googleplacesapi.PlaceDetail
import ru.blackbull.eatogether.googleplacesapi.ResultList


private const val API_KEY: String = "AIzaSyBTkTz_lNZdylZI6vu3jvnJuGfekHxwXBA"

interface TheGooglePlaceApiService {
    @GET("place/details/json")
    fun getPlaceDetail(
        @Query("place_id") placeId: String ,
        @Query("language") language: String = "ru" ,
        @Query("key") apiKey: String = API_KEY ,
        @Query("fields") fields: String = arrayListOf(
            "name" , "rating" , "formatted_phone_number" ,
            "review" , "photos" , "formatted_address" , "opening_hours"
        ).joinToString(",")
    ): Call<OneResult>

    @GET("place/textsearch/json")
    fun getPlacesByName(
        @Query("query") placeName: String ,
        @Query("language") language: String = "ru" ,
        @Query("key") apiKey: String = API_KEY
    ): Call<ResultList>

    @GET("place/nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location: String ,
        @Query("radius") radius: Int = 1000 ,
        @Query("type") type: String = "restaurant",
        @Query("language") language: String = "ru" ,
        @Query("key") apiKey: String = API_KEY
    ): Call<ResultList>

    fun a() {
    }
}
