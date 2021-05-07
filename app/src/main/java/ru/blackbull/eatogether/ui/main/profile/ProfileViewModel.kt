package ru.blackbull.eatogether.ui.main.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<Event<Resource<User?>>>()
    val currentUser: LiveData<Event<Resource<User?>>> = _currentUser

    private val _currentPhoto = MutableLiveData<Uri>()
    val currentPhoto: LiveData<Uri> = _currentPhoto

    fun getCurrentUser() = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
        val foundUser = firebaseRepository.getCurrentUser()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        _currentUser.postValue(Event(foundUser))
    }

    fun signOut() = firebaseRepository.signOut()

    fun updateUser(user: User , photoUri: Uri) = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
//        currentUser.value?.let { event ->
//            user.imageUri = event.peekContent().data?.imageUri
//        }
        firebaseRepository.updateUser(user , photoUri)
        _currentUser.postValue(Event(Resource.Success(user)))
    }

    fun setPhoto(uri: Uri) {
        _currentPhoto.postValue(uri)
    }
}