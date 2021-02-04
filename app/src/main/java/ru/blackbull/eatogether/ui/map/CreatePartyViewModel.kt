package ru.blackbull.eatogether.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.repositories.FirebaseRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreatePartyViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    val createPartyResult = MutableLiveData<Resource<Unit>>()

    fun createParty(
        title: String ,
        description: String ,
        date: String ,
        time: String ,
        placeId: String
    ) = viewModelScope.launch {
        createPartyResult.value = Resource.loading()
        val format = SimpleDateFormat(
            "dd.MM.yyyy HH:mm" , Locale.getDefault()
        )
        val formattedDate: Date?
        try {
            formattedDate = format.parse("$date $time")
        } catch (e: ParseException) {
            createPartyResult.value = Resource.error(
                null ,
                "Введите корректные дату и время"
            )
//            createPartyResult.value = Resource.error(null, R.string.errormessage_date_misformat)
            return@launch
        }
        val party = Party(
            title = title ,
            description = description ,
            time = Timestamp(formattedDate) ,
            placeId = placeId ,
            users = mutableListOf(firebaseRepository.getCurrentUserId())
        )
        val response = firebaseRepository.addParty(party)
        createPartyResult.value = response
    }
}