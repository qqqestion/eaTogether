package ru.blackbull.eatogether.other

/**
 * Класс для использования данных только один раз в LiveData
 * т. е. когда в LiveData попадает значение, а потом происходит переворот экрана
 * подписчики LiveData сработают дважды
 *
 * @param T параметр хранящихся данных
 * @property content данные
 */
class Event<out T>(
    private val content: T
) {

    /**
     * Было ли обработано "событие"
     */
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled() = if (!hasBeenHandled) {
        hasBeenHandled = true
        content
    } else {
        null
    }

    fun peekContent() = content
}