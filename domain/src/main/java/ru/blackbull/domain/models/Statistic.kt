package ru.blackbull.domain.models

/**
 * Класс, описывающий статистику, которая отображается в профиле пользователя
 *
 * @property uniquePlaces количество уникальных заведений
 * @property partyEnded количество законченных компаний
 */
data class Statistic(
    var uniquePlaces: Int? = null ,
    var partyEnded: Int? = null
)
