package ru.blackbull.eatogether.ui.cuisine_choice

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.blackbull.data.models.mapkit.CuisineUi
import ru.blackbull.domain.MapRepository
import ru.blackbull.domain.functional.value
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.other.PlaceManager
import ru.blackbull.eatogether.other.Selector
import javax.inject.Inject

@HiltViewModel
class CuisineChoiceViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    placeManager: PlaceManager,
    private val selector: Selector,
) : BaseViewModel() {

    init {
        mapRepository.getSavedCuisines().onEach(selector::toggle)
    }

    val cuisines: Flow<CuisineChoiceState> = combine(placeManager.cuisines, selector.selected) { apiCuisines, selected ->
        Log.d("!!!", "Selected: $selected")
        CuisineChoiceState(
            apiCuisines.value?.map { CuisineUi(it.id, it.name, it.id in selected) } ?: emptyList()
        )
    }

    fun saveSelected() {
        mapRepository.saveCuisines(selector.getSelectedItems())
    }

    fun toggleCuisine(cuisine: CuisineUi) {
        selector.toggle(cuisine.id)
    }
}