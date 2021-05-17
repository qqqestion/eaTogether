package ru.blackbull.eatogether.other

import timber.log.Timber

/**
 * Пробует выполнить переданную функцию, если любая ОШИБКА ВЫСКАКИВАЕТ, то возвращает Resource.Error
 * с этой ошибкой, если все ОКЕЙ, то возвращает результат функции
 *
 * @param T параметр вложенных данных
 * @param action функция
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
