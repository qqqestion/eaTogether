package ru.blackbull.eatogether.other

object Constants {
    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val MAP_ZOOM = 13f
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val DEFAULT_IMAGE_URL =
        "https://firebasestorage.googleapis.com/v0/b/eatogether-a8490.appspot.com/o/default-avatar.jpeg?alt=media&token=1675c956-67f3-4a67-9061-cf9d3d453e17"

    const val START_SERVICE = "START_SERVICE"
    const val STOP_SERVICE = "STOP_SERVICE"

    // Обновлять локацию раз в ЧАС
    const val TIMER_UPDATE_LOCATION_INTERVAL = 3600000L
//    const val TIMER_UPDATE_LOCATION_INTERVAL = 5000L

    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_NAME = "my_channel_name"
    const val NOTIFICATION_CHANNEL_ID = "my_channel"

    /**
     * В данной константе содержатся всевозможные id категорий заведений Яндекс Карт
     */
    val FOOD_CATEGORIES = listOf("restaurants" , "cafe" , "fast food" , "bars" , "confectionary")

    /*
    type_cuisine -> european_cuisine, russian_cuisine, mixed_cuisine, italian_cuisine, home_cuisine,
    middle_eastern_cuisine, american_cuisine, authors_cuisine, caucasian_cuisine, japanese_cuisine,
    asian_cuisine, meat_cuisine, georgian_cuisine, vegetarian_cuisine, national_cuisine, fish_cuisine,
    uzbek_cuisine, chinese_cuisine, pan_asian_cuisine, shashlik_cuisine, mediterranean_cuisine, irish_cuisine,
    arabian_cuisine, mexican_cuisine, ossetian_cuisine, continental_cuisine, thai_cuisine, latin_american_cuisine,
    kazakh_cuisine, argentine_cuisine, molecular_gastronomy, scandinavian_cuisine, serbian_cuisine,
    vietnamese_cuisine, belarusian_cuisine, indian_cuisine, no_cuisine, tatar_cuisine, international_cuisine,
    slavic_cuisine, ukrainian_cuisine, azerbaijani_cuisine, vegan_cuisine, english_cuisine, african_cuisine,
    german_cuisine, turkish_cuisine, jewish_cuisine, australia_cuisine, french_cuisine, armenian_cuisine,
    kyrgyz_cuisine, czech_cuisine, spanish_cuisine, greek_cuisine, siberian_cuisine, sea_cuisine,
    indonesian_cuisine, korean_cuisine, fusion_cuisine, bulgarian_cuisine_
     */
}