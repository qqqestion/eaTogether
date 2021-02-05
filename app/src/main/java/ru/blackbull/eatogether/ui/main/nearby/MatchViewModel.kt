package ru.blackbull.eatogether.ui.main.nearby

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.repositories.FirebaseRepository
import timber.log.Timber

class MatchViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val user: MutableLiveData<User> = MutableLiveData()

    fun getCurrentUser() = viewModelScope.launch {
        val foundUser = firebaseRepository.getCurrentUser()
        Timber.d("current user: $foundUser")
        user.postValue(foundUser)
    }
}