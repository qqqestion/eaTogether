package ru.blackbull.domain

import ru.blackbull.domain.exceptions.ConfirmPasswordException
import ru.blackbull.domain.exceptions.EmailValidationException
import ru.blackbull.domain.exceptions.PasswordValidationException
import javax.inject.Inject

class UserAuthValidator @Inject constructor() {

    fun validateEmail(email: String) {
        if (email.isEmpty()) {
            throw EmailValidationException()
        }
    }

    fun validatePassword(password: String) {
        if (password.isEmpty()) {
            throw PasswordValidationException()
        }
    }

    fun validatePasswordAndConfirmedPassword(password: String , confirmedPassword: String) {
        if (password != confirmedPassword) {
            throw ConfirmPasswordException()
        }
        validatePassword(password)
    }
}