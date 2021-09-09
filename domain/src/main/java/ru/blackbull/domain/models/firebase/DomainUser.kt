package ru.blackbull.domain.models.firebase

import ru.blackbull.domain.models.Location

data class DomainUser(
    var id: String? = null ,
    var phone: String? = null ,
    var firstName: String? = null ,
    var lastName: String? = null ,
    var description: String? = null ,
    var birthday: Long? = null ,
    var isRegistrationComplete: Boolean = true ,
    var mainImageUri: String? = null ,
    var images: List<String> = listOf() ,
    var likedUsers: List<String> = mutableListOf() ,
    var dislikedUsers: List<String> = mutableListOf() ,
    var friendList: List<String> = mutableListOf() ,
    var lastLocation: Location? = null
)

enum class FriendState {
    UNFRIEND ,
    INVITATION_SENT ,
    FRIEND ,
    ITSELF
}
