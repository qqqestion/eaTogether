package ru.blackbull.eatogether.models.firebase

import com.google.firebase.firestore.DocumentId

data class LunchInvitation(
    @DocumentId
    var id: String? = null ,
    var inviter: String? = null ,
    var invitee: String? = null ,
    var partyId: String? = null
)