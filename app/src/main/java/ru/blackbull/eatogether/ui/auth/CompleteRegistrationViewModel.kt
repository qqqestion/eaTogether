package ru.blackbull.eatogether.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.usecases.CompleteRegistrationUseCase
import ru.blackbull.domain.usecases.CompleteRegistrationUseCaseError
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.ui.auth.fragments.CompleteRegistrationFragmentDirections
import javax.inject.Inject

@HiltViewModel
class CompleteRegistrationViewModel @Inject constructor(
    private val completeRegistration: CompleteRegistrationUseCase,
) : BaseViewModel() {

    private val _state = MutableLiveData<CompleteRegistrationState>()
    val state: LiveData<CompleteRegistrationState> = _state

    fun submitAccountInfo(
        firstName: String,
        lastName: String,
        description: String,
        birthday: Long,
    ) = viewModelScope.launch {
        _state.value = CompleteRegistrationState.Loading
        val result =
            completeRegistration.invoke(firstName, lastName, description, birthday)
        when (result) {
            is Either.Left -> _state.value = CompleteRegistrationState.Error(result.error)
            is Either.Right -> navigate(
                CompleteRegistrationFragmentDirections.actionSetAccountInfoFragmentToMapFragment()
            )
        }
    }
}

sealed class CompleteRegistrationState {

    object Loading : CompleteRegistrationState()

    data class Error(val error: CompleteRegistrationUseCaseError) : CompleteRegistrationState()
}