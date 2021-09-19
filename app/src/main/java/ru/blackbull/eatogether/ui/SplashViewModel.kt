package ru.blackbull.eatogether.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.usecases.IsSignInUseCase
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.UiStateWithData
import ru.blackbull.eatogether.other.failure
import ru.blackbull.eatogether.other.loading
import ru.blackbull.eatogether.other.success
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    private val isSignIn: IsSignInUseCase
) : ViewModel() {

    private val _isSignInStatus = MutableLiveData<UiStateWithData<Boolean>>()
    val isSignInStatus: LiveData<UiStateWithData<Boolean>> = _isSignInStatus

    init {
        isSignIn()
    }

    private fun isSignIn() = viewModelScope.launch {
        _isSignInStatus.value = loading()
        isSignIn.invoke(
            UseCase.None ,
            viewModelScope
        ) {
            it.onFailure { t ->
                _isSignInStatus.value = failure(getError(t))
            }.onSuccess { result ->
                _isSignInStatus.value = success(result)
            }
        }
    }

    private fun getError(t: Throwable) = when (t) {
        else -> R.string.error_default
    }
}

