package ru.blackbull.eatogether.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import javax.inject.Inject

@HiltViewModel
class InviteForLunchViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _friendList = MutableLiveData<Event<Resource<List<User>>>>()
    val friendList: LiveData<Event<Resource<List<User>>>> = _friendList

    fun getFriendList(partyId: String) = viewModelScope.launch {
        _friendList.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getFriendListForParty(partyId)
        _friendList.postValue(Event(response))
    }

    private val _invitationStatus = MutableLiveData<Event<Resource<User>>>()
    val invitationStatus: LiveData<Event<Resource<User>>> = _invitationStatus

    fun sendInvitation(partyId: String , user: User) = viewModelScope.launch {
        _invitationStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.sendLunchInvitation(partyId , user)
        if (response is Resource.Success) {
            _invitationStatus.postValue(Event(Resource.Success(user)))
        } else {
            _invitationStatus.postValue(Event(Resource.Error(response.error , response.msg)))
        }
    }
}