package ru.blackbull.eatogether.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.exceptions.*
import ru.blackbull.domain.models.DomainAuthUser
import ru.blackbull.domain.usecases.SetAccountInfoUseCase
import ru.blackbull.eatogether.R
import javax.inject.Inject

@HiltViewModel
class SetAccountInfoViewModel @Inject constructor(
    private val setAccountInfo: SetAccountInfoUseCase
) : ViewModel() {

    private val _setAccountInfoStatus = MutableLiveData<UiState>()
    val setAccountInfoStatus: LiveData<UiState> = _setAccountInfoStatus

    fun submitAccountInfo(
        firstName: String ,
        lastName: String ,
        description: String ,
        birthday: Long ,
        imageUrl: String
    ) = viewModelScope.launch {
        setAccountInfoStatus.value?.let { if (it is UiState.Loading) return@launch }
        _setAccountInfoStatus.value = loading()
        setAccountInfo.invoke(
            SetAccountInfoUseCase.Params(
                DomainAuthUser(firstName , lastName , description , birthday , imageUrl)
            ) ,
            viewModelScope
        ) {
            it.onFailure { t ->
                _setAccountInfoStatus.value = UiState.Failure(getSignUpError(t))
            }.onSuccess {
                _setAccountInfoStatus.value = success()
            }
        }
    }

    private fun getSignUpError(throwable: Throwable): Int = when (throwable) {
        is FirstNameValidationException -> R.string.error_first_name_is_empty
        is LastNameValidationException -> R.string.error_last_name_is_empty
        is BirthdayValidationException -> R.string.error_too_young
        else -> R.string.error_default
    }
}