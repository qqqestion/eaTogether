package ru.blackbull.eatogether.other

import ru.blackbull.eatogether.BuildConfig

object Constants {
    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val MAP_ZOOM = 13f
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val SEARCH_TIME_DELAY = 500L

    const val GOOGLE_API_KEY: String = BuildConfig.GOOGLE_API_KEY
    const val BASE_GOOGLE_API_URL = "https://maps.googleapis.com/maps/api/"

    const val DEFAULT_IMAGE_URL =
        "https://firebasestorage.googleapis.com/v0/b/eatogether-a8490.appspot.com/o/download.jpeg?alt=media&token=6b9935b4-f29b-4720-92f2-a6f46449b16c"

    const val START_SERVICE = "START_SERVICE"
    const val STOP_SERVICE = "STOP_SERVICE"
    const val TIMER_UPDATE_LOCATION_INTERVAL = 2000L
}