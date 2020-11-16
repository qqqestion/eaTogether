package com.example.testplacesapi.classesForParsingPlaces

class Result(
    var business_status: String,
    var geometry: Geometry,
    var icon: String,
    var name: String,
    var photos: List<Photo>?,
    var place_id: String,
    var rating: Double,
    var reference: String,
    var types: List<String>,
    var utc_offset: Int,
    var vicinity: String,
) {}