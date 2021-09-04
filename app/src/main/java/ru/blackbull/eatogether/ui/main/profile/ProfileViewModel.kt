package ru.blackbull.eatogether.ui.main.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.models.Statistic
import ru.blackbull.data.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.domain.Resource
import ru.blackbull.domain.FirebaseDataSource
import ru.blackbull.domain.models.DomainUser
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepository: FirebaseDataSource
) : ViewModel() {

    private val _currentUser = MutableLiveData<Event<Resource<DomainUser?>>>()
    val currentUser: LiveData<Event<Resource<DomainUser?>>> = _currentUser

    private val _deleteStatus = MutableLiveData<Event<Resource<DomainUser>>>()
    val deleteStatus: LiveData<Event<Resource<DomainUser>>> = _deleteStatus

    fun getCurrentUser() = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
        val foundUser = firebaseRepository.getCurrentUser().toResource()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        _currentUser.postValue(Event(foundUser))
    }

    fun signOut() = firebaseRepository.signOut()

    fun updateUser(user: User) = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.updateUser(user.toDomainUser()).toResource()
        _currentUser.postValue(Event(response))
    }

    fun deleteImage(uri: Uri) = viewModelScope.launch {
        _deleteStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.deleteImage(uri.toString()).toResource()
        _deleteStatus.postValue(Event(response))
    }

    fun makeImageMain(uri: Uri) = viewModelScope.launch {
        firebaseRepository.makeImageMain(uri.toString())
    }

    private val _statisticStatus = MutableLiveData<Event<Resource<Statistic>>>()
    val statisticStatus: LiveData<Event<Resource<Statistic>>> = _statisticStatus

    fun getStatistic() = viewModelScope.launch {
        _statisticStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getStatistic().toResource()
        _statisticStatus.postValue(Event(response))
    }
}