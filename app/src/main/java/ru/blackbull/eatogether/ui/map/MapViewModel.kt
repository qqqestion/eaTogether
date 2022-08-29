package ru.blackbull.eatogether.ui.map

import android.annotation.SuppressLint
import android.os.Looper
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.domain.Constants
import ru.blackbull.domain.MapRepository
import ru.blackbull.domain.functional.error
import ru.blackbull.domain.functional.value
import ru.blackbull.domain.models.Location
import ru.blackbull.eatogether.core.BaseViewModel
import ru.blackbull.eatogether.core.NavigationCommand
import ru.blackbull.eatogether.other.LoadingManager
import ru.blackbull.eatogether.other.MapError
import ru.blackbull.eatogether.other.PlaceManager
import ru.blackbull.eatogether.ui.place_search_result.SearchResultFragmentDirections
import javax.inject.Inject


enum class BottomSheetState {
    Hidden,
    HalfExpanded,
    Expanded
}

data class MapMark(
    val placeId: String,
    val location: Location
)

data class BottomSheet(
    val state: BottomSheetState = BottomSheetState.Hidden,
    val title: String = ""
)

data class MapState(
    val bottomSheet: BottomSheet = BottomSheet(),
    val isLoading: Boolean = false,
    val error: MapError? = null,
    val isPermissionGranted: Boolean = false,
    val currentLocation: Location? = null,
    val isSearched: Boolean = false,
    val marks: List<MapMark> = emptyList()
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placeManager: PlaceManager,
    private val mapRepository: MapRepository,
    private val loadingManager: LoadingManager,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : BaseViewModel(), MapObjectTapListener {

    private var query: String = ""

    private val _bottomSheet = MutableStateFlow(BottomSheet())
    private val _currentLocation = MutableStateFlow<Location?>(null)

    val state = combine(
        placeManager.places,
        _bottomSheet,
        _currentLocation,
        loadingManager.isLoading
    ) { places, bottomSheet, currentLocation, isLoading ->
        if (places.value.isNullOrEmpty().not()) {
            _bottomSheet.value = _bottomSheet.value.copy(state = BottomSheetState.HalfExpanded)
        }
        MapState(
            bottomSheet = bottomSheet,
            isLoading = isLoading,
            error = places.error,
            isPermissionGranted = true,
            currentLocation = currentLocation,
            isSearched = true,
            marks = places.value?.map(PlaceDetail::toMark) ?: emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, MapState())

    fun onPermissionGranted() {
        setupFusedLocationProviderClient()
    }

    fun search(geometry: Geometry, isMapMoving: Boolean = false) {
        if (isMapMoving) return
        placeManager.search(query, geometry, mapRepository.getSavedCuisines())
    }

    fun onQueryChange(newQuery: CharSequence?) {
        query = newQuery?.toString().orEmpty()
    }

    private val _innerNavCommands = MutableSharedFlow<NavigationCommand>(
        0,
        1,
        BufferOverflow.DROP_OLDEST
    )
    val innerNavCommands = _innerNavCommands.asSharedFlow()

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val placeId = mapObject.userData as String
        _innerNavCommands.tryEmit(
            NavigationCommand.To(
                SearchResultFragmentDirections.actionSearchResultToPlaceDetailFragment(placeId)
            )
        )
        return true
    }

    private var isFirstLocation: Boolean = true

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.lastLocation.let { loc ->
                if (isFirstLocation) {
                    _currentLocation.value = Location(loc.latitude, loc.longitude)
                } else {
                    _currentLocation.value = null
                }
                isFirstLocation = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupFusedLocationProviderClient() {
        val request = LocationRequest.create().apply {
            interval = Constants.LOCATION_UPDATE_INTERVAL
            fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCleared() {
        super.onCleared()
        mapRepository.removeSavedCuisines()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}

private fun PlaceDetail.toMark(): MapMark = MapMark(
    id,
    checkNotNull(location)
)
