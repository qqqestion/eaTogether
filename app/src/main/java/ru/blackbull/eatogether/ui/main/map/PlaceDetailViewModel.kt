package ru.blackbull.eatogether.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.repositories.PlaceRepository

class PlaceDetailViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository ,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val placeDetail: MutableLiveData<PlaceDetail> = MutableLiveData()

    val searchParties: MutableLiveData<List<Party>> = MutableLiveData()

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

    fun searchPartyByPlace(partyId: String) = viewModelScope.launch {
        val parties = firebaseRepository.searchPartyByPlace(partyId)
        searchParties.postValue(parties)
    }

    fun addUserToParty(party: Party) = viewModelScope.launch {
        firebaseRepository.addCurrentUserToParty(party)
    }
}