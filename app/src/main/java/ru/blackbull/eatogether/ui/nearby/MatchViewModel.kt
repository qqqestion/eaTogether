package ru.blackbull.eatogether.ui.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.Resource
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.eatogether.other.Event
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val firebaseRepository: UserRepository
) : ViewModel() {

    private val _user: MutableLiveData<Event<Resource<DomainUser>>> = MutableLiveData()
    val user: LiveData<Event<Resource<DomainUser>>> = _user

    fun getCurrentUser() = viewModelScope.launch {
        _user.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.getCurrentUser().toResource()
        Timber.d("current user: ${response.data}")
        _user.postValue(Event(response))
    }
}