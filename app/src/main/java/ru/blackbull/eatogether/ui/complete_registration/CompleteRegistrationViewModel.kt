package ru.blackbull.eatogether.ui.complete_registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.usecases.CompleteRegistrationUseCase
import ru.blackbull.eatogether.core.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CompleteRegistrationViewModel @Inject constructor(
    private val completeRegistration: CompleteRegistrationUseCase,
) : BaseViewModel() {

    private val _state = MutableLiveData<CompleteRegistrationState>()
    val state: LiveData<CompleteRegistrationState> = _state

    fun completeRegistration(
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
