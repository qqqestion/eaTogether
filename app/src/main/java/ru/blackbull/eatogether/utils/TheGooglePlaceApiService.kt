package ru.blackbull.eatogether.utils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.blackbull.eatogether.googleplacesapi.PlaceDetail


private const val API_KEY: String = "AIzaSyBTkTz_lNZdylZI6vu3jvnJuGfekHxwXBA"

interface TheGooglePlaceApiService {
    @GET("place/details/json")
    fun getPlaceDetail(
        @Query("place_id") placeId: String,
        @Query("language") language: String = "ru",
        @Query("key") apiKey: String = API_KEY,
        @Query("fields") fields: String = arrayListOf(
            "name" , "rating" , "formatted_phone_number" ,
            "review" , "photos" , "formatted_address" , "opening_hours"
        ).joinToString(",")
    ): Call<PlaceDetail>
}
