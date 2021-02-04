package ru.blackbull.eatogether.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.repositories.FirebaseRepository


class FirebaseViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    val userParties: MutableLiveData<List<Party>> = MutableLiveData()

    val user: MutableLiveData<User> = MutableLiveData()

    fun getPartiesByCurrentUser() = viewModelScope.launch {
        val parties = firebaseRepository.getPartiesByCurrentUser()
        userParties.postValue(parties)
    }

    fun getCurrentUser() = viewModelScope.launch {
        val foundUser = firebaseRepository.getCurrentUser()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        user.postValue(foundUser)
    }
}
