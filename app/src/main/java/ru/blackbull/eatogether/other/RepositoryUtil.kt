package ru.blackbull.eatogether.other

import timber.log.Timber
import java.lang.Exception


inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Timber.d(e)
        Resource.Error(e)
    }
}
