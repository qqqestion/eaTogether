package ru.blackbull.eatogether.other

import ru.blackbull.eatogether.other.Constants.BASE_GOOGLE_API_URL
import ru.blackbull.eatogether.other.Constants.GOOGLE_API_KEY
import java.text.SimpleDateFormat
import java.util.*

object PhotoUtility {

    fun getPhotoUrl(photoReference: String , width: Int , height: Int): String {
        val url = BASE_GOOGLE_API_URL + "place/photo?" +
                "maxwidth=" + width +
                "&maxheight=" + height +
                "&photoreference=" + photoReference +
                "&key=" + GOOGLE_API_KEY
        return url
    }

    fun getFormattedTime(date: Date): String {
        val formatter = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return formatter.format(date)
    }
}