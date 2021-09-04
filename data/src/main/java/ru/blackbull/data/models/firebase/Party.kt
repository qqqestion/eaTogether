package ru.blackbull.data.models.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import ru.blackbull.domain.models.DomainParty
import java.io.Serializable
import java.util.*

/**
 * Класс, описывающий компанию
 *
 * @property id идентификатор компании
 * @property placeId идентификатор заведения
 * @property time время
 * @property users пользователи в компании
 */
data class Party(
    @DocumentId
    var id: String? = null ,
    val placeId: String? = null ,
    @get:Exclude
    var isCurrentUserInParty: Boolean = false ,
    var time: Timestamp? = null ,
    val users: MutableList<String> = mutableListOf()
) : Serializable {

    fun toDomainParty(): DomainParty {
        return DomainParty(
            id,
            placeId,
            isCurrentUserInParty,
            time?.seconds,
            users
        )
    }
}

fun DomainParty.toParty(): Party {
    return Party(
        id ,
        placeId ,
        isCurrentUserInParty ,
        if (time != null) Timestamp(Date(time!!))
        else null ,
        users
    )
}