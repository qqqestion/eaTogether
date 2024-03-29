package ru.blackbull.eatogether.ui.create_party

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.Party
import ru.blackbull.domain.PartyRepository
import ru.blackbull.domain.Resource
import ru.blackbull.domain.UserRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreatePartyViewModel @Inject constructor(
    private val firebaseRepository: UserRepository,
    private val partyRepository: PartyRepository
) : ViewModel() {

    private val _createPartyResult = MutableLiveData<Resource<Unit>>()
    val createPartyResult: LiveData<Resource<Unit>> = _createPartyResult

    fun createParty(
        datetime: Date,
        placeId: String
    ) = viewModelScope.launch {
        _createPartyResult.value?.let {
            if (it is Resource.Loading) {
                return@launch
            }
        }
        _createPartyResult.value = Resource.Loading()
        val party = Party(
            time = Timestamp(datetime),
            placeId = placeId,
            users = mutableListOf(firebaseRepository.getCurrentUserId())
        )
        val response = partyRepository.addParty(party.toDomainParty()).toResource()
        _createPartyResult.value = response
    }
}