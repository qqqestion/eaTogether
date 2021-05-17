package ru.blackbull.eatogether.models.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.io.Serializable

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
) : Serializable