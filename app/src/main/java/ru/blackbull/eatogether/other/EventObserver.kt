package ru.blackbull.eatogether.other

import androidx.lifecycle.Observer
import ru.blackbull.domain.Resource
import timber.log.Timber

/**
 * Класс для обработки событий с внутренним состоянием данных
 *
 * @param T
 * @property onError что делать при ошибке
 * @property onLoading что делать при загрузке
 * @property onSuccess что делать при успешном выполнении
 */
class EventObserver<T>(
    private inline val onError: ((String) -> Unit)? = null ,
    private inline val onLoading: (() -> Unit)? = null ,
    private inline val onSuccess: (T) -> Unit
) : Observer<Event<Resource<T>>> {

    override fun onChanged(event: Event<Resource<T>>?) {
        when (val content = event?.peekContent()) {
            is Resource.Success -> {
                content.data?.let(onSuccess)
            }
            is Resource.Error -> {
                event.getContentIfNotHandled()?.let {
                    if (it.error != null) {
                        Timber.d(it.error)
                    }
                    onError?.let { error ->
                        error(it.msg ?: it.error?.message.toString())
                    }
                }
            }
            is Resource.Loading -> {
                onLoading?.let { loading ->
                    loading()
                }
            }
        }
    }
}