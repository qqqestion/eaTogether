package ru.blackbull.domain.models

/**
 * Класс, описывающий приглашение в друзья.
 *
 * @property id идентификатор приглашения
 * @property inviter пользователь, которых пригласил в друзья
 * @property invitee пользователь, которого пригласили в друзья
 */
data class DomainInvitationWithUsers(
    var id: String? = null ,
    var inviter: DomainUser? = null ,
    var invitee: DomainUser? = null
)