package ru.blackbull.data

object Faker {

    val user = FakeUser(
        "test@email.com",
        "test1234"
    )

    data class FakeUser(
        val email: String,
        val password: String
    )
}