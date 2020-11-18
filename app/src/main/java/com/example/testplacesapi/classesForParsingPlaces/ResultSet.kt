package com.example.testplacesapi.classesForParsingPlaces

import com.google.gson.annotations.SerializedName

class ResultSet(
    @SerializedName("results") var basicLocations: Array<BasicLocation>,
    var status: String,
) {}