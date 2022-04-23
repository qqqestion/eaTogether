package ru.blackbull.eatogether

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class LoadingManager @Inject constructor() {

    private val counter = AtomicInteger(0)

    private val _isLoading = MutableStateFlow(0)
    val isLoading = _isLoading.map { it == 0 }

    fun startLoading() {
        _isLoading.value = counter.incrementAndGet()
    }

    fun finishLoading() {
        _isLoading.value = counter.decrementAndGet()
    }
}