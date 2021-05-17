package ru.blackbull.eatogether.models

import android.os.Parcel
import android.os.Parcelable

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
    var location: Location? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readValue(Float::class.java.classLoader) as? Float ,
        parcel.readValue(Int::class.java.classLoader) as? Int ,
        parcel.createStringArrayList() ?: listOf() ,
        parcel.createStringArrayList() ?: listOf() ,
    ) {
        location = Location(parcel.readDouble() , parcel.readDouble())
    }

    override fun writeToParcel(parcel: Parcel , flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeString(workingState)
        parcel.writeValue(score)
        parcel.writeValue(ratings)
        parcel.writeStringList(categories)
        parcel.writeStringList(cuisine)
        parcel.writeDouble(location!!.latitude)
        parcel.writeDouble(location!!.longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlaceDetail> {
        override fun createFromParcel(parcel: Parcel): PlaceDetail {
            return PlaceDetail(parcel)
        }

        override fun newArray(size: Int): Array<PlaceDetail?> {
            return arrayOfNulls(size)
        }
    }
}
