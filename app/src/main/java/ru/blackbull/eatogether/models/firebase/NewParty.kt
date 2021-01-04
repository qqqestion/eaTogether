package ru.blackbull.eatogether.models.firebase

import com.google.firebase.Timestamp

data class NewParty(
    var id: String? = null ,
    val title: String? = null ,
    val description: String? = null ,
    val placeId: String? = null ,
    val time: Timestamp? = null ,
    val users: List<String> = listOf()
) {}