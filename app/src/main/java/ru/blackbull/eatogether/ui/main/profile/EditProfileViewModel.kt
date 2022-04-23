package ru.blackbull.eatogether.ui.main.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import ru.blackbull.domain.usecases.*
import ru.blackbull.eatogether.LoadingManager
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.other.Event
import javax.inject.Inject

data class EditProfileState(
    val firstName: String,
    val lastName: String,
    val birthday: Long,
    val description: String,
    val isLoading: Boolean = false,
    val error: ProfileError? = null,
    val statistic: Statistic? = null
)

sealed interface ProfileError

object UploadImageFailure : ProfileError

class ErrorManager<ErrorType : Any> {

    private val _error = MutableStateFlow<Event<ErrorType?>>(Event(null))
    val error = _error.asSharedFlow()

    fun putError(newError: ErrorType) {
        _error.value = Event(newError)
    }
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val signOutUseCase: SignOutUseCase,
    private val loadingManager: LoadingManager,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : BaseViewModel() {

    private val errorManager = ErrorManager<ProfileError>()

    private val _currentUser = MutableLiveData<Event<Resource<DomainUser?>>>()
    val currentUser: LiveData<Event<Resource<DomainUser?>>> = _currentUser

    private val _deleteStatus = MutableLiveData<Event<Resource<DomainUser>>>()
    val deleteStatus: LiveData<Event<Resource<DomainUser>>> = _deleteStatus

    private val _user = MutableStateFlow<DomainUser?>(null)
    private val _statistics = MutableStateFlow<Statistic?>(null)

    val state = combine(
        _user,
        loadingManager.isLoading,
        errorManager.error,
    ) { user, isLoading, error ->
        EditProfileState(
            user?.firstName.orEmpty(),
            user?.lastName.orEmpty(),
            user?.birthday ?: 0L,
            user?.description.orEmpty(),
            isLoading,
            error.getContentIfNotHandled(),
        )
    }

    init {
        fetchCurrentUser()
//        fetchStatistics()
        errorManager.putError(UploadImageFailure)
    }

    private fun fetchCurrentUser() = viewModelScope.launch {
        loadingManager.startLoading()
        when (val result = getUserUseCase()) {
            is Either.Left -> errorManager.putError(result.error.map())
            is Either.Right -> _user.value = result.value
        }
        loadingManager.finishLoading()
    }

    fun signOut() {
        signOutUseCase()
        navigate(EditProfileFragmentDirections.actionEditProfileFragmentToStartFragment())
    }

    fun updateUser(user: User) = viewModelScope.launch {
        loadingManager.startLoading()
        when (val result = updateUserUseCase(user.toDomainUser())) {
            is Either.Left -> errorManager.putError(result.error.map())
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

    private fun fetchStatistics() = viewModelScope.launch {
        _statisticStatus.postValue(Event(Resource.Loading()))
        val response = userRepository.getStatistic().toResource()
        _statisticStatus.postValue(Event(response))
    }

    private fun GetUserError.map(): ProfileError = when (this) {
        NoInternetError -> TODO()
        UnexpectedNetworkCommunicationError -> TODO()
        UserNotFound -> TODO()
    }

    private fun UpdateUserError.map(): ProfileError = when (this) {
        NoInternetError -> TODO()
        UnexpectedNetworkCommunicationError -> TODO()
    }
}