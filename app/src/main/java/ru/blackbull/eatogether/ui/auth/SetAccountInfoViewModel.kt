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

    private val _signUpStatus = MutableLiveData<UiState>()
    val signUpStatus: LiveData<UiState> = _signUpStatus

    fun submitAccountInfo(
        firstName: String ,
        lastName: String ,
        description: String ,
        birthday: Long ,
        imageUrl: String
    ) = viewModelScope.launch {
        signUpStatus.value?.let { if (it is UiState.Loading) return@launch }
        _signUpStatus.value = loading()
        setAccountInfo.invoke(
            SetAccountInfoUseCase.Params(
                DomainAuthUser(firstName , lastName , description , birthday , imageUrl)
            ) ,
            viewModelScope
        ) {
            it.onFailure { t ->
                _signUpStatus.value = UiState.Failure(getSignUpError(t))
            }.onSuccess {
                _signUpStatus.value = success()
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