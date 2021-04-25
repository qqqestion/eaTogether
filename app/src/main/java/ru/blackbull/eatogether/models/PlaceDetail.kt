package ru.blackbull.eatogether.models

data class PlaceDetail(
    val id: String? = "" ,
    val name: String? = "" ,
    val address: String? = "" ,
    val phone: String? = "" ,
    val workingState: String? = "" ,
    val score: Float? = 0F ,
    val ratings: Int? = 0 ,
    val categories: List<String> = listOf() ,
    val features: List<String> = listOf()
)
