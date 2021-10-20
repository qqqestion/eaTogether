package ru.blackbull.domain.models.firebase

import ru.blackbull.domain.models.Location

data class DomainUser(
    val id: String? = null ,
    val firstName: String? = null ,
    val lastName: String? = null ,
    val description: String? = null ,
    val birthday: Long? = null ,
    val isRegistrationComplete: Boolean = true ,
    var mainImageUri: String? = null ,
    var images: List<String> = listOf() ,
    val likedUsers: List<String> = mutableListOf() ,
    val dislikedUsers: List<String> = mutableListOf() ,
    var friendList: List<String> = mutableListOf() ,
    val lastLocation: Location? = null
)

enum class FriendState {
    UNFRIEND ,
    INVITATION_SENT ,
    FRIEND ,
    ITSELF
}
