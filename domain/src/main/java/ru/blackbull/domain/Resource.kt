package ru.blackbull.domain

/**
 * Класс для отображения состояния ответов с репозитория (чаще всего)
 *
 * @param T тип хранящихся данных
 * @property data данные
 * @property msg сообщение ошибки
 * @property error ошибка
 */
sealed class Resource<out T>(
    val data: T? ,
    var msg: String? ,
    val error: Throwable?
) {

    /**
     * Класс, отражающий состояние "Успешно"
     *
     * @param T
     * @constructor
     *
     * @param data данные
     */
    class Success<T>(
        data: T? = null
    ) : Resource<T>(data = data , msg = null , error = null)

    /**
     * Класс, отражающий состояние "Загрузка"
     *
     * @param T
     * @constructor
     *
     * @param data данные
     */
    class Loading<T>(
        data: T? = null
    ) : Resource<T>(data = data , msg = null , error = null)

    /**
     * Класс, отражающий состояние "Ошибка"
     *
     * @param T
     * @constructor
     *
     * @param error ошибка
     * @param msg сообщение ошибки
     * @param data данные
     */
    class Error<T>(
        error: Throwable? = null ,
        msg: String? = null ,
        data: T? = null
    ) : Resource<T>(data = data , msg = msg , error = error)
}
