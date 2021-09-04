package ru.blackbull.data.models.mapkit

/**
 * Класс, описывающий тип кухни
 *
 * @property id идентификатор
 * @property name название
 * @property isChecked была ли выбрана данная кухня
 */
data class CuisineType(
    val id: String ,
    val name: String ,
    var isChecked: Boolean = false
)
