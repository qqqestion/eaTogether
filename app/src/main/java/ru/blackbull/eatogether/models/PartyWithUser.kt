package ru.blackbull.eatogether.models

import com.google.firebase.Timestamp
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User

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
    val users: MutableList<User> = mutableListOf()
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