package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_place_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.snackbar


@AndroidEntryPoint
class PlaceDetailFragment : Fragment(R.layout.fragment_place_detail) {

    private val args: PlaceDetailFragmentArgs by navArgs()

    private val placeDetailViewModel: PlaceDetailViewModel by viewModels()

    private lateinit var placeUri: String

    private lateinit var partiesAdapter: PartyAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        placeUri = args.placeUri

        btnPlaceDetailCreateParty.setOnClickListener {
            findNavController().navigate(
                PlaceDetailFragmentDirections.actionPlaceDetailFragmentToCreatePartyFragment(
                    placeUri ,
                    tvPlaceDetailName.text.toString() ,
                    tvPlaceDetailCategories.text.toString()
                )
            )
        }

        partiesAdapter.setOnItemViewClickListener { party ->
            findNavController().navigate(
                PlaceDetailFragmentDirections.actionPlaceDetailFragmentToPartyDetailFragment(party.id!!)
            )
        }
        partiesAdapter.setOnJoinCLickListener { party ->
            placeDetailViewModel.addUserToParty(party)
            findNavController().navigate(
                PlaceDetailFragmentDirections.actionPlaceDetailFragmentToPartyDetailFragment(party.id!!)
            )
        }

        placeDetailViewModel.getPlaceDetail(placeUri)
        placeDetailViewModel.searchPartyByPlace(placeUri)
    }

    private fun setupRecyclerView() {
        partiesAdapter = PartyAdapter()
        rvPlaceDetailParties.apply {
            adapter = partiesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObservers() {
        placeDetailViewModel.placeDetail.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            } ,
            onLoading = {

            }
        ) { placeDetail ->
            updatePlaceInfo(placeDetail)
        })
        placeDetailViewModel.searchParties.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            } ,
            onLoading = {

            }
        ) { parties ->
            partiesAdapter.parties = parties
        })
    }

    private fun updatePlaceInfo(place: PlaceDetail) {
        tvPlaceDetailName.text = place.name
        tvAddress.text = place.address
        tvPlaceDetailCategories.text = place.categories.joinToString()
        tvPlaceDetailPhone.text = place.phone
        tvWorkingHours.text = place.workingState
        tvScore.text = place.score.toString()
        // TODO: добавить работу с локализацией
        tvRatings.text = "${place.ratings} оценок"
    }
}