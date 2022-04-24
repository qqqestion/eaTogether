package ru.blackbull.eatogether.ui.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.functional.value
import ru.blackbull.domain.models.Statistic
import ru.blackbull.domain.usecases.GetUserUseCase
import ru.blackbull.eatogether.other.LoadingManager
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.ui.edit_profile.ProfileError
import javax.inject.Inject

data class ProfileState(
    val image: String? = null,
    val statistic: Statistic? = null,
    val isLoading: Boolean = true,
    val error: ProfileError? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loadingManager: LoadingManager,
    getUserUseCase: GetUserUseCase
) : BaseViewModel() {

    private val _image = flow {
        emit(getUserUseCase().value?.mainImageUri)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _statistics = flow {
        emit(userRepository.getStatistic().value)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val state: Flow<ProfileState> = combine(_image, _statistics) { image, statistics ->
        ProfileState(
            image,
            statistics,
            false,
        )
    }

    fun onProfileClicked() =
        navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
}