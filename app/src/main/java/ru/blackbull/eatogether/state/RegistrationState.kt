package ru.blackbull.eatogether.state

import com.google.firebase.FirebaseException

sealed class RegistrationState(
    val exception: FirebaseException? = null
) {
    class Success : RegistrationState()
    class Error(exception: FirebaseException) : RegistrationState(exception = exception)
    class Loading : RegistrationState()
}