package ru.blackbull.data.models.firebase

import ru.blackbull.domain.models.firebase.DomainLunchInvitationWithUsers

/**
 * Класс, описывающий приглашение в компанию
 *
 * @property id
 * @property inviter
 * @property invitee
 * @property partyId
 */
data class LunchInvitationWithUsers(
    var id: String? = null ,
    var inviter: User? = null ,
    var invitee: User? = null ,
    var partyId: String? = null
) {

    fun toDomainLunchInvitationWithUsers(): DomainLunchInvitationWithUsers {
        return DomainLunchInvitationWithUsers(
            id ,
            inviter?.toDomainUser() ,
            invitee?.toDomainUser() ,
            partyId
        )
    }
}

fun DomainLunchInvitationWithUsers.toLunchInvitationWithUsers(): LunchInvitationWithUsers {
    return LunchInvitationWithUsers(
        id,
        inviter?.toUser(),
        invitee?.toUser(),
        partyId
    )
}