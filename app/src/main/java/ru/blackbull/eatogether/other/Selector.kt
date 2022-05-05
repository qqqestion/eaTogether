package ru.blackbull.eatogether.other

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class Selector @Inject constructor() {

    private val _selected = MutableStateFlow<Set<String>>(hashSetOf())
    val selected: StateFlow<Set<String>> = _selected.asStateFlow()

    fun getSelectedItems() = selected.value

    fun toggle(id: String) {
        val updated = when (id) {
            in selected.value -> selected.value - id
            else -> selected.value + id
        }
        _selected.value = updated
    }
}