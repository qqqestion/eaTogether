package ru.blackbull.eatogether.ui.place_detail

import ru.blackbull.data.models.firebase.PartyWithUser
import ru.blackbull.data.models.mapkit.PlaceDetail


interface PlaceDetailError

data class PlaceDetailState(
    val place: PlaceDetail? = null,
    val parties: List<PartyWithUser> = emptyList(),
    val isLoading: Boolean,
    val error: PlaceDetailError? = null
)