package ru.blackbull.eatogether.models

import ru.blackbull.eatogether.models.firebase.User

/**
 * Класс, описывающий приглашение в компанию
 *
 * @property id
 * @property inviter
 * @property invitee
 * @property partyId
 */
data class LunchInvitationWithUser(
    var id: String? = null ,
    var inviter: User? = null ,
    var invitee: User? = null ,
    var partyId: String? = null
)
