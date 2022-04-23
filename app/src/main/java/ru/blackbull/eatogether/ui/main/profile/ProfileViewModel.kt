package ru.blackbull.eatogether.ui.main.profile

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.functional.value
import ru.blackbull.domain.models.Statistic
import ru.blackbull.domain.usecases.GetUserUseCase
import ru.blackbull.eatogether.LoadingManager
import ru.blackbull.eatogether.core.BaseViewModel
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
        emit(null)
        emit(getUserUseCase().value?.mainImageUri)
    }
    private val _statistics = flow {
        emit(null)
        emit(userRepository.getStatistic().value)
    }
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