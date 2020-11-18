package com.example.testplacesapi.classesForParsingPlaces

import com.google.gson.annotations.SerializedName

class AdressComponent(
    var long_name: String,
    var short_name: String,
    var types: Array<String>
) {
}