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
import ru.blackbull.domain.models.DomainUser
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val firebaseRepository: FirebaseDataSource
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