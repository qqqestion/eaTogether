package ru.blackbull.eatogether.models

import ru.blackbull.eatogether.models.firebase.User

/**
 * Класс, описывающий приглашение в друзья.
 *
 * @property id идентификатор приглашения
 * @property inviter пользователь, которых пригласил в друзья
 * @property invitee пользователь, которого пригласили в друзья
 */
data class InvitationWithUser(
    var id: String? = null ,
    var inviter: User? = null ,
    var invitee: User? = null
)