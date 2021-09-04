package ru.blackbull.data.models.firebase

import com.google.firebase.firestore.DocumentId

/**
 * Класс, описывающий приглашение в компанию
 *
 * @property id идентификатор приглашения
 * @property inviter идентификатор пользователя, который пригласил в компанию
 * @property invitee идентификатор пользователя, которого пригласили в компанию
 * @property partyId идентификатор компании
 */
data class LunchInvitation(
    @DocumentId
    var id: String? = null ,
    var inviter: String? = null ,
    var invitee: String? = null ,
    var partyId: String? = null
)