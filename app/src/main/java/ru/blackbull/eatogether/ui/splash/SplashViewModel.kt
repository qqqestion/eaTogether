package ru.blackbull.eatogether.ui.splash

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.domain.usecases.CheckAuthenticatedUseCase
import ru.blackbull.domain.usecases.CheckAuthenticatedUseCaseError
import ru.blackbull.eatogether.core.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    private val checkAuthenticated: CheckAuthenticatedUseCase,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            checkAuthenticated.invoke().fold(::handleError, ::handleAuthenticated)
        }
    }

    private fun handleError(error: CheckAuthenticatedUseCaseError) {
        Timber.d("Error: $error")
        navigate(SplashFragmentDirections.actionSplashFragmentToStartFragment())
    }

    private fun handleAuthenticated(isAuthenticated: Boolean) {
        val destination = when {
            isAuthenticated -> SplashFragmentDirections.actionSplashFragmentToMapFragment()
            else -> SplashFragmentDirections.actionSplashFragmentToStartFragment()
        }
        navigate(destination)
    }
}

