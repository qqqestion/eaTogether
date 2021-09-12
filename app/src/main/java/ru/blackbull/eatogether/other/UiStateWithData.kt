package ru.blackbull.eatogether.other

import androidx.annotation.StringRes

sealed class UiStateWithData<T>(
    val data: T?,
    @StringRes var messageId: Int? ,
    val error: Throwable?
) {



    class Success<T>(data: T?) : UiStateWithData<T>(data,null,null)

    class Failure<T>(data: T? = null,messageId: Int? = null, error: Throwable? = null) : UiStateWithData<T>(data,messageId,error)

    class Loading<T>(data: T? = null) : UiStateWithData<T>(data,null,null)
}

