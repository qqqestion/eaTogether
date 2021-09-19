package ru.blackbull.eatogether.other

import androidx.annotation.StringRes

sealed class UiStateWithData<out T> {

    class Success<T>(val data: T) : UiStateWithData<T>()

    class Failure(@StringRes val messageId: Int) : UiStateWithData<Nothing>()

    object Loading : UiStateWithData<Nothing>()
}

fun <T> success(data: T) = UiStateWithData.Success(data)

fun failure(@StringRes messageId: Int) = UiStateWithData.Failure(messageId)

fun loading() = UiStateWithData.Loading