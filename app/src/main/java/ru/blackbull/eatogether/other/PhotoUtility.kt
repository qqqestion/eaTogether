package ru.blackbull.eatogether.other

import ru.blackbull.eatogether.other.Constants.BASE_GOOGLE_API_URL
import ru.blackbull.eatogether.other.Constants.GOOGLE_API_KEY

object PhotoUtility {

    fun getPhotoUrl(photoReference: String , width: Int , height: Int): String {
        val url = BASE_GOOGLE_API_URL + "place/photo?" +
                "maxwidth=" + width +
                "&maxheight=" + height +
                "&photoreference=" + photoReference +
                "&key=" + GOOGLE_API_KEY
        return url
    }
}