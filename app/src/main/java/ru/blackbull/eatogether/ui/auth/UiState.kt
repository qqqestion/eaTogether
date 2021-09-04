package ru.blackbull.eatogether.ui.auth

import androidx.annotation.StringRes

sealed class UiState<T> {

    data class Success<T>(val data: T) : UiState<T>()

    data class Failure<T>(@StringRes val messageId: Int) : UiState<T>()

    class Loading<T> : UiState<T>()
}

fun <T> success(data: T) = UiState.Success(data)

fun <T> failure(messageId: Int) = UiState.Failure<T>(messageId)

fun <T> loading() = UiState.Loading<T>()
