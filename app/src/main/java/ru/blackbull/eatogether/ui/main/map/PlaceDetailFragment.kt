package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_place_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber


@AndroidEntryPoint
class PlaceDetailFragment : Fragment(R.layout.fragment_place_detail) {

    private val viewModel: MapViewModel by activityViewModels()

    private var place: PlaceDetail? = null

    private lateinit var partiesAdapter: PartyAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()

        btnPlaceDetailCreateParty.setOnClickListener {
            val navController = findNavController()
            Timber.d("Current destination: ${navController.currentDestination}")
            if (findNavController().currentDestination != null && place != null) {
                findNavController().navigate(
                    PlaceDetailFragmentDirections.actionPlaceDetailFragmentToCreatePartyFragment(
                        place?.id!! ,
                        place?.name!! ,
                        place?.address!!
                    )
                )
            } else {
                snackbar("Current destination or place is null :/")
            }
        }

        partiesAdapter.setOnItemViewClickListener { party ->
            findNavController().navigate(
                PlaceDetailFragmentDirections.actionPlaceDetailFragmentToPartyDetailFragment(party.id!!)
            )
        }
        partiesAdapter.setOnJoinCLickListener { party ->
            viewModel.addUserToParty(party)
            findNavController().navigate(
                PlaceDetailFragmentDirections.actionPlaceDetailFragmentToPartyDetailFragment(party.id!!)
            )
        }
    }

    private fun setupRecyclerView() {
        partiesAdapter = PartyAdapter()
        rvPlaceDetailParties.apply {
            adapter = partiesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObservers() {
        viewModel.placeDetail.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            }
        ) { placeDetail ->
            place = placeDetail
            updatePlaceInfo()
        })
        viewModel.searchParties.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            }
        ) { parties ->
            partiesAdapter.parties = parties
        })
    }

    private fun updatePlaceInfo() {
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