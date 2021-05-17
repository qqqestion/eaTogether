package ru.blackbull.eatogether.ui.main.lunchinvitations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.LunchInvitationWithUser
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import javax.inject.Inject

@HiltViewModel
class LunchInvitationsViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _lunchInvitations = MediatorLiveData<Event<Resource<List<LunchInvitationWithUser>>>>()
    val lunchInvitations: LiveData<Event<Resource<List<LunchInvitationWithUser>>>> = _lunchInvitations

    fun getLunchInvitations() = viewModelScope.launch {
        _lunchInvitations.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getLunchInvitations()
        _lunchInvitations.postValue(Event(response))
    }
}