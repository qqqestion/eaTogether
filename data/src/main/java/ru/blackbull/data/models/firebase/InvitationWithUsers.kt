package ru.blackbull.data.models.firebase

import ru.blackbull.domain.models.firebase.DomainInvitationWithUsers

/**
 * Класс, описывающий приглашение в друзья.
 *
 * @property id идентификатор приглашения
 * @property inviter пользователь, которых пригласил в друзья
 * @property invitee пользователь, которого пригласили в друзья
 */
data class InvitationWithUsers(
    var id: String? = null ,
    var inviter: User? = null ,
    var invitee: User? = null
) {

    fun toDomainInvitationWithUsers(): DomainInvitationWithUsers {
        return DomainInvitationWithUsers(
            id ,
            inviter?.toDomainUser() ,
            invitee?.toDomainUser()
        )
    }
}

fun DomainInvitationWithUsers.toInvitationWithUsers(): InvitationWithUsers {
    return InvitationWithUsers(
        id,
        inviter?.toUser(),
        invitee?.toUser()
    )
}