package ru.blackbull.eatogether.ui.main.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.FriendState
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
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
        val response = firebaseRepository.addToFriendList(user)
        _addToFriendListStatus.postValue(Event(response))
    }

    fun checkUserStatus(user: User) = viewModelScope.launch {
        _userStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.checkUserStatus(user)
        _userStatus.postValue(Event(response))
    }
}