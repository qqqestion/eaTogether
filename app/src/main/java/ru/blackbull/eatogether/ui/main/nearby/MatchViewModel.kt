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
import timber.log.Timber

class MatchViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _user: MutableLiveData<Event<Resource<User>>> = MutableLiveData()
    val user: LiveData<Event<Resource<User>>> = _user

    fun getCurrentUser() = viewModelScope.launch {
        _user.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getCurrentUser()
        Timber.d("current user: ${response.data}")
        _user.postValue(Event(response))
    }
}