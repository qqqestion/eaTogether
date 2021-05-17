package ru.blackbull.eatogether.models.firebase

import com.google.firebase.firestore.DocumentId

data class Invitation(
    @DocumentId
    var id: String? = null ,
    var inviter: String? = null ,
    var invitee: String? = null
)
