package ru.blackbull.eatogether.ui.main.myparties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.other.Event
import ru.blackbull.domain.Resource
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import javax.inject.Inject

@HiltViewModel
class MyPartyViewModel @Inject constructor(
    private val partyRepository: PartyDataSource
) : ViewModel() {

    private val _userParties: MutableLiveData<Event<Resource<List<DomainPartyWithUser>>>> =
        MutableLiveData()
    val userParties: LiveData<Event<Resource<List<DomainPartyWithUser>>>> = _userParties

    fun getPartiesByCurrentUser() = viewModelScope.launch {
        _userParties.postValue(Event(Resource.Loading()))
        val parties = partyRepository.getPartiesByCurrentUser().toResource()
        _userParties.postValue(Event(parties))
    }
}