package ru.blackbull.eatogether.other

import androidx.lifecycle.Observer
import timber.log.Timber

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