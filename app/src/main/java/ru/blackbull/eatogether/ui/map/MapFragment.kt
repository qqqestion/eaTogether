package ru.blackbull.eatogether.ui.map

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.domain.models.Location
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.core.NavigationCommand
import ru.blackbull.eatogether.core.onTextChanged
import ru.blackbull.eatogether.databinding.FragmentMapBinding
import ru.blackbull.eatogether.other.MapError
import ru.blackbull.eatogether.ui.cuisine_choice.CuisineChoiceDialogFragment

@AndroidEntryPoint
class MapFragment : BaseFragmentV2<MapViewModel>(
    R.layout.fragment_map, MapViewModel::class
), CameraListener {

    private val binding: FragmentMapBinding by viewBinding()

    private val requestPermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                viewModel.onPermissionGranted()
            }
        }

    private val bottomSheetBehavior: BottomSheetBehavior<NestedScrollView> by lazy {
        BottomSheetBehavior.from(binding.bottomSheet.root)
    }

    private val localNavHost: NavHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.childNavFragment) as NavHostFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermission()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::handleState)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.innerNavCommands.collect {
                    when (it) {
                        is NavigationCommand.To -> localNavHost.navController.navigate(
                            it.direction
                        )
                    }
                }
            }
        }

        initMap()


        /**
         * По нажатию на кнопку поиска происходит запрос
         * После клавиатура закрывается
         */
        binding.etMapSearchPlaces.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(binding.etMapSearchPlaces.text.toString())
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.etMapSearchPlaces.onTextChanged(viewModel::onQueryChange)
        binding.fab.setOnClickListener {
            CuisineChoiceDialogFragment.showOnlyOnce(childFragmentManager)
        }
        binding.bottomSheet.ivCancel.setOnClickListener {
            handlePressBack()
        }
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.fab.isVisible = slideOffset < 0.5
            }
        })
        binding.bottomSheet.clBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.bottomSheet.clBottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val index = 3
                val hidden = binding.bottomSheet.clBottomSheet.getChildAt(index)
                bottomSheetBehavior.peekHeight = hidden.top
            }
        })

        /**
         * TODO: Возможно баг при нажатии назад с других основных экранов
         */
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handlePressBack()
                }
            }
        )
    }

    private fun initMap() {
        binding.yandexMapView.map?.addCameraListener(this)
        binding.yandexMapView.map.logo.setAlignment(
            Alignment(
                HorizontalAlignment.RIGHT,
                VerticalAlignment.TOP
            )
        )
        val mapKit = MapKitFactory.getInstance()
        val userLocationLayer = mapKit.createUserLocationLayer(binding.yandexMapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
    }

    private fun handlePressBack() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            findNavController().popBackStack()
        } else {
            /**
             * .popBackStack возвращает true, если был произведен переход, иначе false, но при имении только одного фрагмента в backstack
             * .popBackStack его удаляет и делает невозможным дальнейшее использование (был баг с открытием нижнего меню два раза и
             * IllegalStateException при повторном использовании)
             * MapFragment -> select item on map -> *opens bottom sheet menu* -> press back
             * -> select item on map again -> click create party -> IllegalStateException because there is no current destination
             * in nested nav controller (mBackStack is empty)
             */
            if (localNavHost.navController.currentDestination?.id == R.id.searchResultFragment) {
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.yandexMapView.map.mapObjects.clear()
                binding.etMapSearchPlaces.setText("")
            } else {
                localNavHost.navController.popBackStack()
            }
        }
    }

    private fun handleState(state: MapState) {
        bottomSheetBehavior.state = when (state.bottomSheet.state) {
            BottomSheetState.Hidden -> BottomSheetBehavior.STATE_HIDDEN
            BottomSheetState.HalfExpanded -> BottomSheetBehavior.STATE_HALF_EXPANDED
            BottomSheetState.Expanded -> BottomSheetBehavior.STATE_EXPANDED
        }
        state.error?.let(::handleError)
        state.currentLocation?.let(::handleLocation)
        if (state.isSearched) hideKeyboard()
        if (state.isLoading) binding.yandexMapView.map.mapObjects.clear()
        handleMarks(state.marks)
    }

    private fun handleMarks(marks: List<MapMark>) {
        val mapObjects = binding.yandexMapView.map.mapObjects
        mapObjects.clear()
        for (mark in marks) {
            mapObjects.addPlacemark(
                Point(
                    mark.location.latitude,
                    mark.location.longitude
                ),
                ImageProvider.fromResource(
                    requireContext(),
                    R.drawable.search_result
                )
            ).apply {
                userData = mark.placeId
                addTapListener(viewModel)
            }
        }
    }

    private fun handleLocation(location: Location) {
        binding.yandexMapView.map.move(
            CameraPosition(
                Point(location.latitude, location.longitude),
                13.0f,
                0.0f,
                0.0f
            )
        )
    }

    private fun handleError(error: MapError) {

    }

    private fun submitQuery(query: String, isMapMoving: Boolean = false) {
        if (query.isEmpty()) {
            return
        }
        viewModel.search(
            VisibleRegionUtils.toPolygon(binding.yandexMapView.map.visibleRegion),
            isMapMoving
        )
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameroUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        submitQuery(binding.etMapSearchPlaces.text.toString(), !finished)
    }

    private fun requestPermission() {
        requestPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.yandexMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        binding.yandexMapView.map.removeCameraListener(this)
        binding.yandexMapView.onStop()
    }
}