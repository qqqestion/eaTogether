package ru.blackbull.eatogether.other

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class LoadingManager @Inject constructor() {

    private val counter = AtomicInteger(0)

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun startLoading() {
        _isLoading.value = counter.incrementAndGet() != 0
    }

    fun finishLoading() {
        _isLoading.value = counter.decrementAndGet() != 0
    }
}