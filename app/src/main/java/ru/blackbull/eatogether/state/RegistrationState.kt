package ru.blackbull.eatogether.state

sealed class RegistrationState(
    val exception: Exception? = null
) {
    class Success : RegistrationState()
    class Error(exception: Exception? = null) : RegistrationState(exception = exception)
    class Loading : RegistrationState()
}