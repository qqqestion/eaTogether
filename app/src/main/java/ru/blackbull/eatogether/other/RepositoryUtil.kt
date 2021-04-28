package ru.blackbull.eatogether.other

import timber.log.Timber


/**
 * Tries to perform action and returns its Resource, if error occurs returns Resource.Error
 *
 * @param T the type of Resource
 * @param action action to perform to get Resource
 * @return Resource
 */
inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Timber.d(e)
        Resource.Error(e)
    }
}
