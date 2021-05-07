package ru.blackbull.eatogether.models.firebase

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Класс, описывающий уведомление
 *
 * @property id идентификатор уведомления
 * @property userId идентификатор пользователя
 * @property type типа уведомления
 */
class Notification(
    @DocumentId
    var id: String? = null ,
    var userId: String? = null ,
    var type: String
) : Serializable
