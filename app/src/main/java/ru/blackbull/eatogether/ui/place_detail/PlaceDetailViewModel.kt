package ru.blackbull.eatogether.ui.place_detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.PartyWithUser
import ru.blackbull.data.models.firebase.toPartyWithUser
import ru.blackbull.domain.PartyRepository
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.functional.Right
import ru.blackbull.domain.functional.map
import ru.blackbull.domain.functional.value
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import ru.blackbull.domain.usecases.GetPartiesByPlaceUseCase
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.other.LoadingManager
import ru.blackbull.eatogether.other.PlaceManager
import ru.blackbull.eatogether.ui.edit_profile.ErrorManager

class PlaceDetailViewModel @AssistedInject constructor(
    @Assisted private val placeId: String,
    private val partyRepository: PartyRepository,
    private val getPartiesByPlaceUseCase: GetPartiesByPlaceUseCase,
    placeManager: PlaceManager,
    loadingManager: LoadingManager,
) : BaseViewModel() {

    private val errorManager = ErrorManager<PlaceDetailError>()

    private val _parties = flow {
        Log.d("!!!", "parties: start")
        emit(getPartiesByPlaceUseCase(placeId).map { parties -> parties.map(DomainPartyWithUser::toPartyWithUser) })
        Log.d("!!!", "parties: end")
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Either.Right(emptyList()))
    private val _place =
        placeManager.placeDetail.stateIn(viewModelScope, SharingStarted.Eagerly, Right(null))

    val state = combine(
        _parties,
        _place,
        loadingManager.isLoading,
        errorManager.error
    ) { parties, placeDetail, isLoading, error ->
        Log.d("!!!", "state is ready: $parties, $placeDetail")
        PlaceDetailState(
            placeDetail.value,
            parties.value ?: emptyList(),
            isLoading,
            error.getContentIfNotHandled()
        )
    }

    init {
        placeManager.fetchPlaceDetail(placeId)
    }

    fun addUserToParty(party: PartyWithUser) = viewModelScope.launch {
        partyRepository.addCurrentUserToParty(party.toParty().toDomainParty())
        navigateToPartyDetail(party)
    }

    fun navigateToPartyDetail(party: PartyWithUser) {
        navigate(
            PlaceDetailFragmentDirections.actionPlaceDetailFragmentToPartyDetailFragment(party.id!!)
        )
    }

    fun navigateToCreateParty() {
        _place.value.value?.let { place ->
            navigate(
                PlaceDetailFragmentDirections.actionPlaceDetailFragmentToCreatePartyFragment(
                    place.id,
                    place.name,
                    place.address
                )
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(placeId: String): PlaceDetailViewModel
    }
}