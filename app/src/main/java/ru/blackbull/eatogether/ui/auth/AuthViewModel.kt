package ru.blackbull.eatogether.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repository.FirebaseRepository
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AuthViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    val signInResult = MutableLiveData<Resource<Boolean>>()

    val signUpResult = MutableLiveData<Resource<Unit>>()

    fun isAuthenticated(): Boolean = firebaseRepository.isAuthenticated()

    fun signIn(email: String , password: String) = viewModelScope.launch {
        signInResult.postValue(Resource.loading(null))
        val response = firebaseRepository.signIn(email , password)
        signInResult.postValue(response)
    }

    fun signUp(
        email: String ,
        password: String ,
        firstName: String ,
        lastName: String ,
        birthday: String ,
        description: String
    ) = viewModelScope.launch {
        signUpResult.value = (Resource.loading(null))
//        val app = getApplication<Application>()
        for (field in listOf(email , password , firstName , lastName , birthday , description)) {
            if (field.isEmpty()) {
                signUpResult.value = Resource.error(
                    null ,
                    "Заполните поля" ,
//                    app.getString(R.string.errormessage_fields_must_be_filled) ,
                    null
                )
                return@launch
            }
        }

        val user = User()
        user.email = email
        user.firstName = firstName
        user.lastName = lastName
        user.description = description

        val formatter = SimpleDateFormat("dd.MM.yyyy" , Locale.getDefault())
        try {
            user.birthday = Timestamp(formatter.parse(birthday))
        } catch (e: ParseException) {
            signUpResult.value = Resource.error(
                null ,
                "Введите правильную дату" ,
//                app.getString(R.string.errormessage_date_misformat) ,
                null
            )
            return@launch
        }
        signUpWithValidUser(user , password)
    }

    private fun signUpWithValidUser(userInfo: User , password: String) = viewModelScope.launch {
        val response = firebaseRepository.signUpWithEmailAndPassword(userInfo , password)
        signUpResult.postValue(response)
    }
}