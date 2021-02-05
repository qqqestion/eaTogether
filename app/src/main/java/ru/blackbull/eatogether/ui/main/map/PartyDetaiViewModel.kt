package ru.blackbull.eatogether.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.repositories.PlaceRepository

class PartyDetailViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository ,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val selectedParty: MutableLiveData<Party> = MutableLiveData()
    val partyParticipants: MutableLiveData<List<User>> = MutableLiveData()

    val placeDetail: MutableLiveData<PlaceDetail> = MutableLiveData()

    fun getPartyParticipants(party: Party) = viewModelScope.launch {
        val participants = firebaseRepository.getPartyParticipants(party)
        partyParticipants.postValue(participants)
    }

    fun getPartyById(id: String) = viewModelScope.launch {
        val party = firebaseRepository.getPartyById(id)
        selectedParty.postValue(party)
    }

    fun getPlaceDetail(placeId: String) = viewModelScope.launch {
        val response = placeRepository.getPlaceDetail(placeId)
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.status == "OK") {
                    placeDetail.postValue(it.placeDetail)
                }
            }
        }
    }
}