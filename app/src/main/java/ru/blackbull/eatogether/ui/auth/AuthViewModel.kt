package ru.blackbull.eatogether.ui.auth

import android.app.Application
import android.content.Context
import android.widget.ImageView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.EaTogetherApplication
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.ui.main.snackbar
import java.lang.Exception
import java.sql.SQLClientInfoException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AuthViewModel @ViewModelInject constructor(
    val firebaseRepository: FirebaseRepository ,
    app: Application
) : AndroidViewModel(app) {

    private val _signInResult = MutableLiveData<Event<Resource<Unit>>>()
    val signInResult: LiveData<Event<Resource<Unit>>> = _signInResult

    private val _signUpResult = MutableLiveData<Event<Resource<Unit>>>()
    val signUpResult: LiveData<Event<Resource<Unit>>> = _signUpResult

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
        email: String ,
        password: String ,
        firstName: String ,
        lastName: String ,
        birthday: String ,
        description: String
    ): User {
        for (field in listOf(email , password , firstName , lastName , birthday , description)) {
            if (field.isEmpty()) {
                throw Exception()
            }
        }

        val user = User()
        user.email = email
        user.firstName = firstName
        user.lastName = lastName
        user.description = description
        val formatter = SimpleDateFormat("dd.MM.yyyy" , Locale.getDefault())
        val date = formatter.parse(birthday)
        user.birthday = Timestamp(date!!)
        return user
    }

    fun signUp(
        email: String ,
        password: String ,
        firstName: String ,
        lastName: String ,
        birthday: String ,
        description: String
    ) = viewModelScope.launch {
        _signUpResult.value?.let {
            if (it.peekContent() is Resource.Loading) {
                return@launch
            }
        }
        _signUpResult.value = Event(Resource.Loading())
        val app = getApplication<EaTogetherApplication>()
        val user: User
        try {
            user = validateUser(
                email ,
                password ,
                firstName ,
                lastName ,
                birthday ,
                description
            )
        } catch (e: ParseException) {
            _signUpResult.value = Event(
                Resource.Error(msg = app.getString(R.string.errormessage_date_misformat))
            )
            return@launch
        } catch (e: Exception) {
            _signUpResult.value = Event(
                Resource.Error(msg = app.getString(R.string.errormessage_fields_must_be_filled))
            )
            return@launch
        }
        val response = firebaseRepository.signUpWithEmailAndPassword(user , password)
        if (response is Resource.Error) {
            val stringId = when (response.error) {
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
            response.msg = msg
        }
        _signUpResult.postValue(Event(response))
    }
}
