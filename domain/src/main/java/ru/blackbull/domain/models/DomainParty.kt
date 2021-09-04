package ru.blackbull.domain.models

data class DomainParty(
    var id: String? = null ,
    val placeId: String? = null ,
    var isCurrentUserInParty: Boolean = false ,
    var time: Long? = null ,
    val users: MutableList<String> = mutableListOf()
)
