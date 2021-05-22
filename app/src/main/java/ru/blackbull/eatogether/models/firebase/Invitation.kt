package ru.blackbull.eatogether.models.firebase

import com.google.firebase.firestore.DocumentId

/**
 * Класс, описывающий приглашение в друзья
 *
 * @property id
 * @property inviter идентификатор пользователя, который пригласил в компанию
 * @property invitee идентификатор пользователя, которого пригласили в компанию
 */
data class Invitation(
    @DocumentId
    var id: String? = null ,
    var inviter: String? = null ,
    var invitee: String? = null
)
