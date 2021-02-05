package ru.blackbull.eatogether.models.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Party(
    @DocumentId
    var id: String? = null ,
    val title: String? = null ,
    val description: String? = null ,
    val placeId: String? = null ,
    val time: Timestamp? = null ,
    val users: MutableList<String> = mutableListOf()
) : Serializable