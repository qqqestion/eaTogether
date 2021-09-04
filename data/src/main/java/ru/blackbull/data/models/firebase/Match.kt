package ru.blackbull.data.models.firebase

import com.google.firebase.firestore.DocumentId

/**
 * Класс, описывающий "совпадение" из тиндера
 *
 * @property id идентификатор совпадения
 * @property firstLiker кто первый лайкнул
 * @property secondLiker кто второй лайкнул
 * @property isProcessed было ли обработано
 */
data class Match(
    @DocumentId
    var id: String? = null ,
    var firstLiker: String? = null ,
    var secondLiker: String? = null ,
    var isProcessed: Boolean = false
)