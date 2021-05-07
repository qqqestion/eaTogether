package ru.blackbull.eatogether.models.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Класс, описывающий компанию
 *
 * @property id идентификатор компании
 * @property title название
 * @property description описания
 * @property placeId идентификатор заведения
 * @property time время
 * @property users пользователи в компании
 */
data class Party(
    @DocumentId
    var id: String? = null ,
    val title: String? = null ,
    val description: String? = null ,
    val placeId: String? = null ,
    val time: Timestamp? = null ,
    val users: MutableList<String> = mutableListOf()
) : Serializable