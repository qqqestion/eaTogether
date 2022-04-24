package ru.blackbull.eatogether.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.InvitationWithUsers
import ru.blackbull.domain.Resource
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.models.firebase.DomainInvitationWithUsers
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.models.firebase.FriendState
import ru.blackbull.eatogether.other.Event
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val firebaseRepository: UserRepository
) : ViewModel() {

    private val _friendList = MutableLiveData<Event<Resource<List<DomainUser>>>>()
    val friendList: LiveData<Event<Resource<List<DomainUser>>>> = _friendList

    fun getFriendList() = viewModelScope.launch {
        _friendList.postValue(Event(Resource.Success()))
        val response = firebaseRepository.getFriendList().toResource()
        _friendList.postValue(Event(response))
    }

    private val _invitationList = MutableLiveData<Event<Resource<List<DomainInvitationWithUsers>>>>()
    val invitationList: LiveData<Event<Resource<List<DomainInvitationWithUsers>>>> = _invitationList

    fun getInvitationList() = viewModelScope.launch {
        _invitationList.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getInvitationList().toResource()
        _invitationList.postValue(Event(response))
    }

    private val _addToFriendListStatus = MutableLiveData<Event<Resource<InvitationWithUsers>>>()
    val addToFriendList: LiveData<Event<Resource<InvitationWithUsers>>> = _addToFriendListStatus

    fun addToFriendList(invitation: InvitationWithUsers) = viewModelScope.launch {
        if (_addToFriendListStatus.value?.peekContent() is Resource.Loading) {
            return@launch
        }
        _addToFriendListStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.addToFriendList(invitation.inviter!!.toDomainUser()).toResource()
        if (response.data == FriendState.FRIEND) {
            _addToFriendListStatus.postValue(Event(Resource.Success(invitation)))
        } else {
            _addToFriendListStatus.postValue(Event(Resource.Error(response.error , response.msg)))
        }
    }
}