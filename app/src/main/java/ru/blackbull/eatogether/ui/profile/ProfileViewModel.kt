package ru.blackbull.eatogether.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.repositories.FirebaseRepository

class ProfileViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    val currentUser = MutableLiveData<User?>()

    fun getCurrentUser() = viewModelScope.launch {
        val foundUser = firebaseRepository.getCurrentUser()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        currentUser.postValue(foundUser)
    }

    fun signOut() = firebaseRepository.signOut()

    fun isAuthenticated() = firebaseRepository.isAuthenticated()

    fun updateUser(updatedUser: User) = viewModelScope.launch {
        updatedUser.imageUri = currentUser.value?.imageUri
        firebaseRepository.updateUser(updatedUser)
        currentUser.postValue(updatedUser)
    }

}