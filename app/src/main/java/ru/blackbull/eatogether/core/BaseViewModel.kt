package ru.blackbull.eatogether.core

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class NavigationCommand {
    data class To(val direction: NavDirections) : NavigationCommand()
}

abstract class BaseViewModel : ViewModel() {

    private val _navigationCommands = MutableSharedFlow<NavigationCommand>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationCommands = _navigationCommands.asSharedFlow()

    fun navigate(direction: NavDirections) {
        _navigationCommands.tryEmit(NavigationCommand.To(direction))
    }
}
