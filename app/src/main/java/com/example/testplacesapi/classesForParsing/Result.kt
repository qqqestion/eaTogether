package com.example.testplacesapi.classesForParsing

class Result(
    var business_status: String,
    var geometry: Geometry,
    var icon: String,
    var name: String,
    var photos: Array<Photo>,
    var place_id: String,
    var rating: Double,
    var reference: String,
    var types: Array<String>,
    var utc_offset: Int,
    var vicinity: String,
){}