package com.example.testplacesapi.classesForParsingPlaceDetails

import com.example.testplacesapi.classesForParsingPlaces.Geometry
import com.example.testplacesapi.classesForParsingPlaces.Photo

class ResultDetails(
    var business_status: String,
    var name: String,
    var photos: List<Photo>?,
    var opening_hours: OpeningHours?,
    var rating: Double,
    var types: List<String>,
    var reviews: List<Review>?,
    var formatted_address: String,
    var formatted_phone_number: String,
) {

}