package ru.blackbull.eatogether.models

import com.google.firebase.Timestamp
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User

data class PartyWithUser(
    var id: String? = null ,
    val placeId: String? = null ,
    var isCurrentUserInParty: Boolean = false ,
    var time: Timestamp? = null ,
    val users: MutableList<User> = mutableListOf()
) {

    fun toParty(): Party {
        return Party(
            id,
            placeId,
            isCurrentUserInParty,
            time,
            users.map { it.id!! }.toMutableList()
        )
    }
}