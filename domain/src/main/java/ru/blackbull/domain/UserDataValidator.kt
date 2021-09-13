package ru.blackbull.domain

import ru.blackbull.domain.exceptions.BirthdayValidationException
import ru.blackbull.domain.exceptions.FirstNameValidationException
import ru.blackbull.domain.exceptions.LastNameValidationException
import java.util.*
import javax.inject.Inject

class UserDataValidator @Inject constructor() {

    fun validateFirstName(firstName: String) {
        if (firstName.isEmpty()) {
            throw FirstNameValidationException()
        }
    }

    fun validateLastName(lastName: String) {
        if (lastName.isEmpty()) {
            throw LastNameValidationException()
        }
    }

    fun validateDescription(description: String) {
        /* no-op */
    }

    fun validateBirthday(birthday: Long) {
        val eighteenYearsAge = Calendar.getInstance()
        eighteenYearsAge.time = Date()
        eighteenYearsAge.set(Calendar.YEAR , eighteenYearsAge.get(Calendar.YEAR) - 18)
        if (eighteenYearsAge.time.before(Date(birthday))) {
            throw BirthdayValidationException()
        }
    }
}