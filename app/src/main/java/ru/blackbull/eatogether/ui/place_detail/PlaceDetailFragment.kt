package ru.blackbull.eatogether.ui.place_detail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_place_detail.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.core.BaseFragmentV2
import timber.log.Timber
import javax.inject.Inject

class MyFactory(
    private val create: () -> ViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return create() as T
    }
}

@AndroidEntryPoint
class PlaceDetailFragment : BaseFragmentV2<PlaceDetailViewModel>(
    R.layout.fragment_place_detail,
    PlaceDetailViewModel::class
) {
    @Inject
    lateinit var factory: PlaceDetailViewModel.Factory

    private val args: PlaceDetailFragmentArgs by navArgs()

    override val factoryProducer: () -> ViewModelProvider.Factory
        get() = { MyFactory { factory.create(args.placeId) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val partiesAdapter = PartyAdapter()
        rvPlaceDetailParties.apply {
            adapter = partiesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    partiesAdapter.parties = state.parties
                    updatePlaceInfo(state.place)
                }
            }
        }

        btnPlaceDetailCreateParty.setOnClickListener {
            viewModel.navigateToCreateParty()
        }

        partiesAdapter.setOnItemViewClickListener(viewModel::navigateToPartyDetail)
        partiesAdapter.setOnJoinCLickListener(viewModel::addUserToParty)
    }

    private fun updatePlaceInfo(place: PlaceDetail?) {
        place?.let { nnplace ->
            Timber.d("$nnplace")
            tvAddress.text = nnplace.address
            tvPlaceDetailCategories.text = nnplace.categories.joinToString()
            tvWorkingHours.text = nnplace.workingState
            tvScore.text = nnplace.score.toString()
            // TODO: добавить работу с локализацией
            tvRatings.text = "${nnplace.ratings} оценок"
            tvCuisine.text = nnplace.cuisine.joinToString()
            Timber.d("$nnplace")
        }
    }
}