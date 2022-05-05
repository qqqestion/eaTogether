package ru.blackbull.data.models.mapkit

import android.os.Parcel
import android.os.Parcelable
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.BusinessRating1xObjectMetadata
import ru.blackbull.domain.models.Location

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
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val workingState: String = "",
    val score: Float = 0F,
    val ratings: Int = 0,
    val categories: List<String> = listOf(),
    val cuisine: List<String> = listOf(),
    val location: Location? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        checkNotNull(parcel.readString()),
        checkNotNull(parcel.readString()),
        checkNotNull(parcel.readString()),
        checkNotNull(parcel.readString()),
        checkNotNull(parcel.readString()),
        checkNotNull(parcel.readFloat()),
        checkNotNull(parcel.readInt()),
        parcel.createStringArrayList() ?: listOf(),
        parcel.createStringArrayList() ?: listOf(),
        location = Location(parcel.readDouble(), parcel.readDouble())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeString(workingState)
        parcel.writeFloat(score)
        parcel.writeInt(ratings)
        parcel.writeStringList(categories)
        parcel.writeStringList(cuisine)
        location?.let { loc ->
            parcel.writeDouble(loc.latitude)
            parcel.writeDouble(loc.longitude)
        }
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

fun GeoObject.toPlaceDetail(): PlaceDetail {
    val businessMetadata = metadataContainer.getItem(BusinessObjectMetadata::class.java)
    val ratingMetadata = metadataContainer.getItem(BusinessRating1xObjectMetadata::class.java)
    val id = businessMetadata.oid
    val score = ratingMetadata?.score
    val ratings = ratingMetadata?.ratings
    val name = businessMetadata.name
    val address = businessMetadata.address.formattedAddress
    val phones = businessMetadata.phones.map { it.formattedNumber }
    val workingHours = businessMetadata.workingHours?.state?.text
    val categories = businessMetadata.categories.map { it.name }
    val cuisine = businessMetadata.features
        .find { it.id == "type_cuisine" }
        ?.value
        ?.enumValue
        ?.map { it.name }
        ?: listOf()
    val links = businessMetadata.links
    val geometry = geometry.firstOrNull()?.point
    val location = if (geometry != null) {
        Location(geometry.latitude, geometry.longitude)
    } else {
        null
    }
    return PlaceDetail(
        id,
        name,
        address,
        phones.firstOrNull().orEmpty(),
        workingHours.orEmpty(),
        score ?: 0f,
        ratings ?: 0,
        categories,
        cuisine,
        location
    )
}
