package ru.blackbull.eatogether.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.blackbull.eatogether.models.googleplaces.OneResult
import ru.blackbull.eatogether.models.googleplaces.ResultList
import ru.blackbull.eatogether.other.Constants.GOOGLE_API_KEY


interface GooglePlaceApiService {

    @GET("place/details/json")
    suspend fun getPlaceDetail(
        @Query("place_id") placeId: String ,
        @Query("language") language: String = "ru" ,
        @Query("key") apiKey: String = GOOGLE_API_KEY ,
        @Query("fields") fields: String = arrayListOf(
            "name" , "rating" , "formatted_phone_number" ,
            "review" , "photos" , "formatted_address" , "opening_hours"
        ).joinToString(",")
    ): OneResult

    @GET("place/textsearch/json")
    suspend fun getPlacesByName(
        @Query("query") placeName: String ,
        @Query("language") language: String = "ru" ,
        @Query("key") apiKey: String = GOOGLE_API_KEY
    ): ResultList

    @GET("place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String ,
        @Query("radius") radius: Int = 1000 ,
        @Query("type") type: String = "restaurant" ,
        @Query("language") language: String = "ru" ,
        @Query("key") apiKey: String = GOOGLE_API_KEY
    ): ResultList
}
