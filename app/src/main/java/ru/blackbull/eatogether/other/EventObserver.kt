package ru.blackbull.eatogether.other

import androidx.lifecycle.Observer

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
                    onError?.let { error ->
                        error(it.msg!!)
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