package ru.blackbull.eatogether.other

import androidx.annotation.StringRes

sealed class UiStateWithData<out T> {

    class Success<T>(val data: T) : UiStateWithData<T>()

    class Failure(val messageId: Int) : UiStateWithData<Nothing>()

    object Loading : UiStateWithData<Nothing>()
}

