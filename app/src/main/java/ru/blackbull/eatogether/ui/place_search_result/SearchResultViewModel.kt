package ru.blackbull.eatogether.ui.place_search_result

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.domain.functional.error
import ru.blackbull.domain.functional.value
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.other.PlaceManager
import javax.inject.Inject

sealed interface SearchResultError

data class SearchResultState(
    val places: List<PlaceDetail> = emptyList(),
    val error: SearchResultError? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val placeManager: PlaceManager
) : BaseViewModel() {

    val state = placeManager.places.map { either ->
        val error = when (either.error) {
            else -> null
        }
        SearchResultState(either.value ?: emptyList(), error, false)
    }.stateIn(viewModelScope, SharingStarted.Lazily, SearchResultState())

    fun onItemClick(place: PlaceDetail) {
        navigate(
            SearchResultFragmentDirections.actionSearchResultToPlaceDetailFragment(
                place.id
            )
        )
    }
}