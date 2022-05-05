package ru.blackbull.data.models.mapkit

/**
 * Класс, описывающий тип кухни
 *
 * @property id идентификатор
 * @property name название
 * @property isChecked была ли выбрана данная кухня
 */
data class CuisineUi(
    val id: String,
    val name: String,
    val isChecked: Boolean = false
)
