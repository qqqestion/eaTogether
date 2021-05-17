package ru.blackbull.eatogether.ui.auth

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.EaTogetherApplication
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val firebaseRepository: FirebaseRepository ,
    app: Application
) : AndroidViewModel(app) {

    private val _signInResult = MutableLiveData<Event<Resource<Unit>>>()
    val signInResult: LiveData<Event<Resource<Unit>>> = _signInResult

    private val _signUpResult = MutableLiveData<Event<Resource<Unit>>>()
    val signUpResult: LiveData<Event<Resource<Unit>>> = _signUpResult

    val isRegistrationComplete = MutableLiveData<Boolean>()

    fun checkIsRegistrationComplete() = viewModelScope.launch {
        val user = firebaseRepository.getCurrentUser().data
        if (user != null) {
            isRegistrationComplete.postValue(user.isRegistrationComplete)
        } else {
            isRegistrationComplete.postValue(false)
        }
    }

    fun signIn(email: String , password: String) = viewModelScope.launch {
        _signInResult.value?.let {
            if (it.peekContent() is Resource.Loading) {
                return@launch
            }
        }
        _signInResult.postValue(Event(Resource.Loading()))
        val response = firebaseRepository.signIn(email , password)
        _signInResult.postValue(Event(response))
    }

    private fun validateUser(
        firstName: String ,
        lastName: String ,
        birthday: Date ,
        description: String
    ): User {
        for (field in listOf(firstName , lastName , description)) {
            if (field.isEmpty()) {
                throw Exception()
            }
        }

        val user = User()
        user.firstName = firstName
        user.lastName = lastName
        user.description = description
        // TODO: сделать валидацию даты рождения
        user.birthday = Timestamp(birthday)
        return user
    }

    fun signUp(
        firstName: String ,
        lastName: String ,
        birthday: Date ,
        description: String
    ) = viewModelScope.launch {
        _signUpResult.value?.let {
            if (it.peekContent() is Resource.Loading) {
                return@launch
            }
        }
        _signUpResult.postValue(Event(Resource.Loading()))
        val app = getApplication<EaTogetherApplication>()
        val user: User
        try {
            user = validateUser(
                firstName ,
                lastName ,
                birthday ,
                description
            )
        } catch (e: Exception) {
            Timber.d("Validation error: field(s) is empty")
            _signUpResult.postValue(
                Event(
                    Resource.Error(msg = app.getString(R.string.errormessage_fields_must_be_filled))
                )
            )
            return@launch
        }
        Timber.d("Validation complete")
        val response = firebaseRepository.signUpWithEmailAndPassword(user)
        if (response is Resource.Error) {
            val stringId = when (response.error) {
                is FirebaseNetworkException ->
                    R.string.errormessage_network_error
                is FirebaseAuthWeakPasswordException ->
                    R.string.errormessage_weak_password
                is FirebaseAuthInvalidCredentialsException ->
                    R.string.errormessage_email_malformed
                is FirebaseAuthUserCollisionException ->
                    R.string.errormessage_user_already_exists
                else -> null
            }
            val msg = if (stringId == null) {
                response.msg ?: app.getString(R.string.errormessage_unknown_error)
            } else {
                app.getString(stringId)
            }
            Timber.d("Error: $msg")
            response.msg = msg
        }
        Timber.d("Response: $response")
        _signUpResult.postValue(Event(response))
    }
}
