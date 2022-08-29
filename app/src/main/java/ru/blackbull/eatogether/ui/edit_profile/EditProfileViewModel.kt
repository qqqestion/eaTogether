package ru.blackbull.eatogether.ui.edit_profile

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
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.usecases.*
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.LoadingManager
import javax.inject.Inject

data class EditProfileState(
    val firstName: String,
    val lastName: String,
    val description: String,
    val images: List<Uri>,
    val isLoading: Boolean = false,
    val error: ProfileError? = null,
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

    private val _deleteStatus = MutableLiveData<Event<Resource<DomainUser>>>()
    val deleteStatus: LiveData<Event<Resource<DomainUser>>> = _deleteStatus

    private val _user = MutableStateFlow<DomainUser?>(null)

    val state = combine(
        _user,
        loadingManager.isLoading,
        errorManager.error,
    ) { user, isLoading, error ->
        EditProfileState(
            firstName = user?.firstName.orEmpty(),
            lastName = user?.lastName.orEmpty(),
            description = user?.description.orEmpty(),
            images = user?.images?.map { Uri.parse(it) } ?: emptyList(),
            isLoading = isLoading,
            error = error.getContentIfNotHandled(),
        )
    }

    init {
        fetchCurrentUser()
        errorManager.putError(UploadImageFailure)
    }

    fun handleFirstName(text: CharSequence?) {
        _user.value = _user.value?.copy(
            firstName = text?.toString().orEmpty()
        )
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

    fun onSave() {

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

        loadingManager.startLoading()
        userRepository.deleteImage(uri.toString())
        loadingManager.finishLoading()
    }

    fun makeImageMain(uri: Uri) = viewModelScope.launch {
        userRepository.makeImageMain(uri.toString())
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