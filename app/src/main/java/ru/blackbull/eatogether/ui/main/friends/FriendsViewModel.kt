package ru.blackbull.eatogether.ui.main.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.InvitationWithUser
import ru.blackbull.eatogether.models.firebase.FriendState
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _friendList = MutableLiveData<Event<Resource<List<User>>>>()
    val friendList: LiveData<Event<Resource<List<User>>>> = _friendList

    fun getFriendList() = viewModelScope.launch {
        _friendList.postValue(Event(Resource.Success()))
        val response = firebaseRepository.getFriendList()
        _friendList.postValue(Event(response))
    }

    private val _invitationList = MutableLiveData<Event<Resource<List<InvitationWithUser>>>>()
    val invitationList: LiveData<Event<Resource<List<InvitationWithUser>>>> = _invitationList

    fun getInvitationList() = viewModelScope.launch {
        _invitationList.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getInvitationList()
        _invitationList.postValue(Event(response))
    }

    private val _addToFriendListStatus = MutableLiveData<Event<Resource<InvitationWithUser>>>()
    val addToFriendList: LiveData<Event<Resource<InvitationWithUser>>> = _addToFriendListStatus

    fun addToFriendList(invitation: InvitationWithUser) = viewModelScope.launch {
        if (_addToFriendListStatus.value?.peekContent() is Resource.Loading) {
            return@launch
        }
        _addToFriendListStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.addToFriendList(invitation.inviter!!)
        if (response.data == FriendState.FRIEND) {
            _addToFriendListStatus.postValue(Event(Resource.Success(invitation)))
        } else {
            _addToFriendListStatus.postValue(Event(Resource.Error(response.error , response.msg)))
        }
    }
}