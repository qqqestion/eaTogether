package ru.blackbull.eatogether.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections

sealed class NavigationCommand {
    data class To(val direction: NavDirections) : NavigationCommand()
}

abstract class BaseViewModel : ViewModel() {

    private val _navigationCommands = MutableLiveData<NavigationCommand>()
    val navigationCommands: LiveData<NavigationCommand> = _navigationCommands

    fun navigate(direction: NavDirections) {
        _navigationCommands.value = NavigationCommand.To(direction)
    }
}
