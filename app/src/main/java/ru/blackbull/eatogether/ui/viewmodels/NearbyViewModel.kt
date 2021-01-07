package ru.blackbull.eatogether.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.repository.FirebaseRepository
import ru.blackbull.eatogether.state.RegistrationState

class NearbyViewModel : ViewModel() {
    private val firebaseRepository = FirebaseRepository()

    val nearbyUsers: MutableLiveData<List<User>> = MutableLiveData()

    init {
        getNearbyUsers()
    }

    fun getNearbyUsers() = viewModelScope.launch {
        val users = firebaseRepository.getNearbyUsers()
        nearbyUsers.postValue(users)
    }

    fun likeUser(user: User) = viewModelScope.launch {
        val likedUser = firebaseRepository.likeUser(user)
        RegistrationState.Success()
    }

    fun dislikeUser(user: User) = viewModelScope.launch {
        firebaseRepository.dislikeUser(user)
    }
}