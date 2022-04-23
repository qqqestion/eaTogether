package ru.blackbull.eatogether.ui.main.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.User
import ru.blackbull.domain.Resource
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.Statistic
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.usecases.SignOutUseCase
import ru.blackbull.eatogether.LoadingManager
import ru.blackbull.eatogether.other.Event
import javax.inject.Inject

data class ProfileState(
    val firstName: String,
    val lastName: String,
    val birthday: Long,
    val description: String,
    val isLoading: Boolean = false,
    val error: ProfileError? = null,
    val isSignIn: Boolean = true,
    val statistic: Statistic? = null
)

sealed interface ProfileError

class ErrorManager<ErrorType : Any> {

    private val _error = MutableSharedFlow<ErrorType?>(
        0,
        1,
        BufferOverflow.DROP_OLDEST
    )
    val error = _error.asSharedFlow()

    init {
        _error.tryEmit(null)
    }

    fun putError(newError: ErrorType) {
        _error.tryEmit(newError)
    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val signOutUseCase: SignOutUseCase,
    private val loadingManager: LoadingManager,
) : ViewModel() {

    private val errorManager = ErrorManager<ProfileError>()

    private val _currentUser = MutableLiveData<Event<Resource<DomainUser?>>>()
    val currentUser: LiveData<Event<Resource<DomainUser?>>> = _currentUser

    private val _deleteStatus = MutableLiveData<Event<Resource<DomainUser>>>()
    val deleteStatus: LiveData<Event<Resource<DomainUser>>> = _deleteStatus

    private val _signInStatus = MutableStateFlow(true)
    private val _user = MutableStateFlow<DomainUser?>(null)
    val state = combine(
        _user,
        loadingManager.isLoading,
        errorManager.error,
        _signInStatus
    ) { user, isLoading, error, isSignIn ->
        ProfileState(
            user?.firstName.orEmpty(),
            user?.lastName.orEmpty(),
            user?.birthday ?: 0L,
            user?.description.orEmpty(),
            isLoading,
            error,
            isSignIn
        )
    }

    init {
        getCurrentUser()
        getStatistic()
    }

    fun getCurrentUser() = viewModelScope.launch {
//        _currentUser.postValue(Event(Resource.Loading()))
//        val foundUser = userRepository.getCurrentUser().toResource()
//        Log.d("ImageDebug", "viewModel user: $foundUser")
//        _currentUser.postValue(Event(foundUser))
        loadingManager.startLoading()
        when (val result = userRepository.getCurrentUser()) {
            is Either.Left -> errorManager.putError(result.error)
            is Either.Right -> _user.value = result.value
        }
        loadingManager.finishLoading()
    }

    fun signOut() {
        signOutUseCase()
        _signInStatus.value = false
    }

    fun updateUser(user: User) = viewModelScope.launch {
//        _currentUser.postValue(Event(Resource.Loading()))
//        val response = userRepository.updateUser(user.toDomainUser()).toResource()
//        _currentUser.postValue(Event(response))
        loadingManager.startLoading()
        when (val result = userRepository.updateUser(user.toDomainUser())) {
            is Either.Left -> errorManager.putError(result.error)
            is Either.Right -> _user.value = result.value
        }
        loadingManager.finishLoading()
    }

    fun deleteImage(uri: Uri) = viewModelScope.launch {
        _deleteStatus.postValue(Event(Resource.Loading()))
        val response = userRepository.deleteImage(uri.toString()).toResource()
        _deleteStatus.postValue(Event(response))
//        loadingManager.startLoading()
    }

    fun makeImageMain(uri: Uri) = viewModelScope.launch {
        userRepository.makeImageMain(uri.toString())
    }

    private val _statisticStatus = MutableLiveData<Event<Resource<Statistic>>>()
    val statisticStatus: LiveData<Event<Resource<Statistic>>> = _statisticStatus

    private fun getStatistic() = viewModelScope.launch {
        _statisticStatus.postValue(Event(Resource.Loading()))
        val response = userRepository.getStatistic().toResource()
        _statisticStatus.postValue(Event(response))
    }
}