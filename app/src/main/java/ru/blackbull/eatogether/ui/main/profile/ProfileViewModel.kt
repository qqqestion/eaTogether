package ru.blackbull.eatogether.ui.main.profile

import android.util.Log
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

class ProfileViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<Event<Resource<User?>>>()
    val currentUser: LiveData<Event<Resource<User?>>> = _currentUser

    fun getCurrentUser() = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
        val foundUser = firebaseRepository.getCurrentUser()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        _currentUser.postValue(Event(foundUser))
    }

    fun signOut() = firebaseRepository.signOut()

    fun isAuthenticated() = firebaseRepository.isAuthenticated()

    fun updateUser(updatedUser: User) = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
        currentUser.value?.let { event ->
            updatedUser.imageUri = event.peekContent().data?.imageUri
        }
        firebaseRepository.updateUser(updatedUser)
        _currentUser.postValue(Event(Resource.Success(updatedUser)))
    }

}