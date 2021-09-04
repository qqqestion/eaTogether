package ru.blackbull.data.models.firebase

import com.google.firebase.Timestamp
import ru.blackbull.domain.models.DomainPartyWithUser
import java.sql.Time
import java.util.*

/**
 * Класс, описывающий компанию.
 * Вместо идентификаторов пользователей, имеет список самих пользователей
 *
 * @property id
 * @property placeId
 * @property isCurrentUserInParty
 * @property time
 * @property users
 */
data class PartyWithUser(
    var id: String? = null ,
    val placeId: String? = null ,
    var isCurrentUserInParty: Boolean = false ,
    var time: Timestamp? = null ,
    val users: List<User> = listOf()
) {

    fun toParty(): Party {
        return Party(
            id ,
            placeId ,
            isCurrentUserInParty ,
            time ,
            users.map { it.id!! }.toMutableList()
        )
    }
}

fun DomainPartyWithUser.toPartyWithUser(): PartyWithUser {
    return PartyWithUser(
        id ,
        placeId ,
        isCurrentUserInParty ,
        if (time != null) Timestamp(Date(time!!)) else null ,
        users.map { it.toUser() }
    )
}