package ru.blackbull.domain.models

data class DomainAuthUser(
    val firstName: String ,
    val lastName: String ,
    val description: String ,
    val birthday: Long ,
    val imageUrl: String
)
