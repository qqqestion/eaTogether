package ru.blackbull.eatogether.models.mappers

import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.BusinessRating1xObjectMetadata
import ru.blackbull.eatogether.models.Location
import ru.blackbull.eatogether.models.PlaceDetail
import javax.inject.Inject

/**
 * Класс для создания PlaceDetail из com.yandex.mapkit.GeoObject
 * Берет нужную информацию из последнего и передаёт ее в конструктор первого
 *
 */
class PlaceDetailMapper @Inject constructor() {

    fun toPlaceDetail(geoObject: GeoObject): PlaceDetail {
        val businessMetadata =
            geoObject.metadataContainer.getItem(BusinessObjectMetadata::class.java)
        val ratingMetadata =
            geoObject.metadataContainer.getItem(BusinessRating1xObjectMetadata::class.java)
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
        val geometry = geoObject.geometry.firstOrNull()?.point
        val location = if (geometry != null) {
            Location(geometry.latitude , geometry.longitude)
        } else {
            null
        }
        return PlaceDetail(
            id ,
            name ,
            address ,
            phones.firstOrNull() ,
            workingHours ,
            score ,
            ratings ,
            categories ,
            cuisine ,
            location
        )
    }
}