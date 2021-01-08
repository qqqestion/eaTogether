package ru.blackbull.eatogether.models.firebase

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

class Notification(
    @DocumentId
    var id: String? = null ,
    var userId: String? = null,
    var type: String

) : Serializable
