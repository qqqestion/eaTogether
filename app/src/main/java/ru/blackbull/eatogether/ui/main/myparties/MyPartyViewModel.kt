package ru.blackbull.eatogether.ui.main.myparties

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.repositories.FirebaseRepository

class MyPartyViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val userParties: MutableLiveData<List<Party>> = MutableLiveData()

    fun getPartiesByCurrentUser() = viewModelScope.launch {
        val parties = firebaseRepository.getPartiesByCurrentUser()
        userParties.postValue(parties)
    }
}