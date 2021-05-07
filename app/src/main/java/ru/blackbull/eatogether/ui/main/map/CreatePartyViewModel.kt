package ru.blackbull.eatogether.ui.main.map

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreatePartyViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository ,
    private val app: Application
) : ViewModel() {

    private val _createPartyResult = MutableLiveData<Resource<Unit>>()
    val createPartyResult: LiveData<Resource<Unit>> = _createPartyResult

    fun createParty(
        title: String ,
        description: String ,
        datetime: Date ,
        placeId: String
    ) = viewModelScope.launch {
        _createPartyResult.value?.let {
            if (it is Resource.Loading) {
                return@launch
            }
        }
        _createPartyResult.value = Resource.Loading()
        val format = SimpleDateFormat(
            "dd.MM.yyyy HH:mm" , Locale.getDefault()
        )
        val formattedDate: Date?
//        try {
//            formattedDate = format.parse("$date $time")
//        } catch (e: ParseException) {
//            _createPartyResult.value =
//                Resource.Error(msg = app.getString(R.string.errormessage_date_misformat))
//            return@launch
//        }
        val party = Party(
            title = title ,
            description = description ,
            time = Timestamp(datetime) ,
            placeId = placeId ,
            users = mutableListOf(firebaseRepository.getCurrentUserId())
        )
        val response = firebaseRepository.addParty(party)
        _createPartyResult.value = response
    }
}