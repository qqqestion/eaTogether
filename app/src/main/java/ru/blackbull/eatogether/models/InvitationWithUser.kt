package ru.blackbull.eatogether.models

import ru.blackbull.eatogether.models.firebase.User

data class InvitationWithUser(
    var id: String? = null ,
    var inviter: User? = null ,
    var invitee: User? = null
)