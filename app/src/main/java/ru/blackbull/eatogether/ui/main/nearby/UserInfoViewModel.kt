package ru.blackbull.eatogether.ui.main.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.User
import ru.blackbull.domain.FirebaseDataSource
import ru.blackbull.domain.models.firebase.FriendState
import ru.blackbull.eatogether.other.Event
import ru.blackbull.domain.Resource
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val firebaseRepository: FirebaseDataSource
) : ViewModel() {

    private val _addToFriendListStatus = MutableLiveData<Event<Resource<FriendState>>>()
    val addToFriendListStatus: LiveData<Event<Resource<FriendState>>> = _addToFriendListStatus

    private val _userStatus = MutableLiveData<Event<Resource<FriendState>>>()
    val userStatus: LiveData<Event<Resource<FriendState>>> = _userStatus

    fun addToFriendList(user: User) = viewModelScope.launch {
        if (_addToFriendListStatus.value?.peekContent() is Resource.Loading) {
            return@launch
        }
        _addToFriendListStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.addToFriendList(user.toDomainUser()).toResource()
        _addToFriendListStatus.postValue(Event(response))
    }

    fun checkUserStatus(user: User) = viewModelScope.launch {
        _userStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.checkUserStatus(user.toDomainUser()).toResource()
        _userStatus.postValue(Event(response))
    }
}