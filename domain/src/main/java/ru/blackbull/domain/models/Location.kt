package ru.blackbull.domain.models

/**
 * Класс, описывающий местоположение в координатах
 *
 * @property latitude широта
 * @property longitude долгота
 */
data class Location(
    val latitude: Double ,
    val longitude: Double
)