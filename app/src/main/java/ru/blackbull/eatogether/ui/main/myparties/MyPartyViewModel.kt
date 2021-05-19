package ru.blackbull.eatogether.ui.main.myparties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.PartyWithUser
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import javax.inject.Inject

@HiltViewModel
class MyPartyViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _userParties: MutableLiveData<Event<Resource<List<PartyWithUser>>>> =
        MutableLiveData()
    val userParties: LiveData<Event<Resource<List<PartyWithUser>>>> = _userParties

    fun getPartiesByCurrentUser() = viewModelScope.launch {
        _userParties.postValue(Event(Resource.Loading()))
        val parties = firebaseRepository.getPartiesByCurrentUser()
        _userParties.postValue(Event(parties))
    }
}