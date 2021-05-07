package ru.blackbull.eatogether.models

import java.io.Serializable

/**
 * Класс, описывающий заведение
 *
 * @property id идентификатор
 * @property name название
 * @property address адрес
 * @property phone номер телефона
 * @property workingState состояние работы (открыто до 22:00, круглосуточно и тд.)
 * @property score средняя оценка
 * @property ratings количество оценок
 * @property categories категории (ресторан, быстрое питание)
 * @property cuisine кухни, которые используют в этом заведении (русскую, европейскую)
 */
data class PlaceDetail(
    val id: String? = "" ,
    val name: String? = "" ,
    val address: String? = "" ,
    val phone: String? = "" ,
    val workingState: String? = "" ,
    val score: Float? = 0F ,
    val ratings: Int? = 0 ,
    val categories: List<String> = listOf() ,
    val cuisine: List<String> = listOf() ,
    val location: Location? = null
) : Serializable