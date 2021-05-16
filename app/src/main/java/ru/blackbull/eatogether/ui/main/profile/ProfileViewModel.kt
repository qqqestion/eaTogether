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

    private val _deleteStatus = MutableLiveData<Event<Resource<User>>>()
    val deleteStatus: LiveData<Event<Resource<User>>> = _deleteStatus

    fun getCurrentUser() = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
        val foundUser = firebaseRepository.getCurrentUser()
        Log.d("ImageDebug" , "viewModel user: $foundUser")
        _currentUser.postValue(Event(foundUser))
    }

    fun signOut() = firebaseRepository.signOut()

    fun updateUser(user: User) = viewModelScope.launch {
        _currentUser.postValue(Event(Resource.Loading()))
//        currentUser.value?.let { event ->
//            user.imageUri = event.peekContent().data?.imageUri
//        }
        val response = firebaseRepository.updateUser(user)
        _currentUser.postValue(Event(response))
    }

    fun setPhoto(uri: Uri) {
        _currentPhoto.postValue(uri)
    }

    fun deleteImage(uri: Uri) = viewModelScope.launch {
        _deleteStatus.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.deleteImage(uri)
        _deleteStatus.postValue(Event(response))
    }

    fun makeImageMain(uri: Uri) = viewModelScope.launch {
        firebaseRepository.makeImageMain(uri)
    }
}