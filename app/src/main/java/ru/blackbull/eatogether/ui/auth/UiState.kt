package ru.blackbull.eatogether.ui.auth

import androidx.annotation.StringRes

sealed class UiState {

    object Success : UiState()

    data class Failure(@StringRes val messageId: Int) : UiState()

    object Loading : UiState()
}

fun success() = UiState.Success

fun failure(messageId: Int) = UiState.Failure(messageId)

fun loading() = UiState.Loading
