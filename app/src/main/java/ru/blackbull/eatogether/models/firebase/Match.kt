package ru.blackbull.eatogether.models.firebase

import com.google.firebase.firestore.DocumentId

data class Match(
    @DocumentId
    var id: String? = null ,
    var firstLiker: String? = null ,
    var secondLiker: String? = null ,
    var isProcessed: Boolean = false
)