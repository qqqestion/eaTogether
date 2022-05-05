package ru.blackbull.eatogether.core

import android.view.KeyEvent
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
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

fun EditText.onTextChanged(onText: (CharSequence?) -> Unit) {
    doOnTextChanged { text, _, _, _ -> onText(text) }
}