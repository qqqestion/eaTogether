package ru.blackbull.eatogether.ui.lunchinvitations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.Resource
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.models.firebase.DomainLunchInvitationWithUsers
import ru.blackbull.eatogether.other.Event
import javax.inject.Inject

@HiltViewModel
class LunchInvitationsViewModel @Inject constructor(
    private val firebaseRepository: UserRepository
) : ViewModel() {

    private val _lunchInvitations = MediatorLiveData<Event<Resource<List<DomainLunchInvitationWithUsers>>>>()
    val lunchInvitations: LiveData<Event<Resource<List<DomainLunchInvitationWithUsers>>>> = _lunchInvitations

    fun getLunchInvitations() = viewModelScope.launch {
        _lunchInvitations.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getLunchInvitations().toResource()
        _lunchInvitations.postValue(Event(response))
    }
}