package ru.blackbull.eatogether.ui.main.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.domain.Resource
import ru.blackbull.domain.FirebaseDataSource
import ru.blackbull.domain.models.firebase.DomainUser
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NearbyViewModel @Inject constructor(
    private val firebaseRepository: FirebaseDataSource
) : ViewModel() {

    private val _nearbyUsers = MutableLiveData<Event<Resource<MutableList<DomainUser>>>>()
    val nearbyUsers: LiveData<Event<Resource<MutableList<DomainUser>>>> = _nearbyUsers

    private val _likedUser = MutableLiveData<Event<Resource<DomainUser?>>>()
    val likedUser: LiveData<Event<Resource<DomainUser?>>> = _likedUser

    fun getNearbyUsers() = viewModelScope.launch {
        _nearbyUsers.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getNearbyUsers().toResource()
        _nearbyUsers.postValue(Event(response))
    }

    fun likeUser(user: User) = viewModelScope.launch {
        _likedUser.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.likeUser(user.toDomainUser()).toResource()
        _likedUser.postValue(Event(response))
    }

    fun dislikeUser(user: User) = viewModelScope.launch {
        firebaseRepository.dislikeUser(user.toDomainUser())
    }

    suspend fun getUser(uid: String): DomainUser? {
        val response = firebaseRepository.getUser(uid).toResource()
        if (response is Resource.Error) {
            Timber.d(response.error)
        }
        return response.data
    }
}