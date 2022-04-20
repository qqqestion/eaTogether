package ru.blackbull.eatogether.ui

import android.view.KeyEvent
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.clearError() {
    setErrorMessage(null)
}

fun TextInputLayout.setErrorMessage(errorMessage: String?) {
    error = errorMessage
    isErrorEnabled = errorMessage != null
}

val TextInputEditText.trimmedText: String
    get() = text?.toString()?.trim().orEmpty()

fun TextInputEditText.onKeyEnter(body: () -> Unit) {
    setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            body()
            true
        } else {
            false
        }
    }
}