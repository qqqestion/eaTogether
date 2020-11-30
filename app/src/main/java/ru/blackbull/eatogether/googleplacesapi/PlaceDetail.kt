package ru.blackbull.eatogether.googleplacesapi

data class PlaceDetail(
    var business_status: String,
    var name: String,
    var photos: List<Photo>?,
    var opening_hours: OpeningHours?,
    var rating: Double,
    var types: List<String>,
    var reviews: List<Review>?,
    var formatted_address: String,
    var formatted_phone_number: String,
) {

    fun getIsOpen(): Boolean? {
        return opening_hours?.open_now
    }
}