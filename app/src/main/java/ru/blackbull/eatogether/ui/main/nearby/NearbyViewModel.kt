package ru.blackbull.eatogether.ui.main.nearby

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository

class NearbyViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _nearbyUsers = MutableLiveData<Event<Resource<MutableList<User>>>>()
    val nearbyUsers: LiveData<Event<Resource<MutableList<User>>>> = _nearbyUsers

    private val _likedUser = MutableLiveData<Event<Resource<User?>>>()
    val likedUser: LiveData<Event<Resource<User?>>> = _likedUser

    fun getNearbyUsers() = viewModelScope.launch {
        _nearbyUsers.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getNearbyUsers()
        _nearbyUsers.postValue(Event(response))
    }

    fun likeUser(user: User) = viewModelScope.launch {
        _likedUser.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.likeUser(user)
        _likedUser.postValue(Event(response))
    }

    fun dislikeUser(user: User) = viewModelScope.launch {
        firebaseRepository.dislikeUser(user)
    }

    fun sendLikeNotification(user: User) = viewModelScope.launch {
        firebaseRepository.sendLikeNotification(user)
    }
}