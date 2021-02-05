package ru.blackbull.eatogether.other

class Event<out T>(
    private val content: T
) {

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