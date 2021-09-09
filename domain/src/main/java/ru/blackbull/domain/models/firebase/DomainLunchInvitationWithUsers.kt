package ru.blackbull.domain.models.firebase

data class DomainLunchInvitationWithUsers(
    var id: String? = null ,
    var inviter: DomainUser? = null ,
    var invitee: DomainUser? = null ,
    var partyId: String? = null
)
