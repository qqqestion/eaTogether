package ru.blackbull.eatogether.ui.place_detail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.core.ViewModelFactory
import ru.blackbull.eatogether.databinding.FragmentPlaceDetailBinding
import javax.inject.Inject

@AndroidEntryPoint
class PlaceDetailFragment : BaseFragmentV2<PlaceDetailViewModel>(
    R.layout.fragment_place_detail,
    PlaceDetailViewModel::class
) {

    private val binding: FragmentPlaceDetailBinding by viewBinding()

    @Inject
    lateinit var factory: PlaceDetailViewModel.Factory

    private val args: PlaceDetailFragmentArgs by navArgs()

    override val factoryProducer: () -> ViewModelProvider.Factory
        get() = { ViewModelFactory { factory.create(args.placeId) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val partiesAdapter = PartyAdapter()
        binding.rvPlaceDetailParties.apply {
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

        binding.btnPlaceDetailCreateParty.setOnClickListener {
            viewModel.navigateToCreateParty()
        }

        partiesAdapter.setOnItemViewClickListener(viewModel::navigateToPartyDetail)
        partiesAdapter.setOnJoinCLickListener(viewModel::addUserToParty)
    }

    private fun updatePlaceInfo(place: PlaceDetail?) {
        place?.let { nnplace ->
            binding.tvPlaceName.text = nnplace.name
            binding.tvAddress.text = nnplace.address
            binding.tvPlaceDetailCategories.text = nnplace.categories.joinToString()
            binding.tvWorkingHours.text = nnplace.workingState
            binding.tvScore.text = nnplace.score.toString()
            binding.tvRatings.text = "${nnplace.ratings} оценок"
            binding.tvCuisine.text = nnplace.cuisine.joinToString()
        }
    }
}