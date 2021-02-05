package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_place_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.adapters.ReviewAdapter
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.other.PhotoUtility.getPhotoUrl
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber


@AndroidEntryPoint
class PlaceDetailFragment : Fragment(R.layout.fragment_place_detail) {

    private val args: PlaceDetailFragmentArgs by navArgs()

    private val placeDetailViewModel: PlaceDetailViewModel by viewModels()

    private lateinit var placeId: String

    private lateinit var partiesAdapter: PartyAdapter
    private lateinit var reviewsAdapter: ReviewAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        placeId = args.placeId

        btnPlaceDetailCreateParty.setOnClickListener {
            findNavController().navigate(
                PlaceDetailFragmentDirections.actionPlaceDetailFragmentToCreatePartyFragment(
                    placeId ,
                    tvPlaceDetailName.text.toString() ,
                    tvPlaceDetailAddress.text.toString()
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

        placeDetailViewModel.getPlaceDetail(placeId)
        placeDetailViewModel.searchPartyByPlace(placeId)
    }

    private fun setupRecyclerView() {
        partiesAdapter = PartyAdapter()
        rvPlaceDetailParties.apply {
            adapter = partiesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        reviewsAdapter = ReviewAdapter()
        rvPlaceDetailReviews.apply {
            adapter = reviewsAdapter
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
        tvPlaceDetailAddress.text = place.formatted_address
        tvPlaceDetailPhone.text = place.formatted_phone_number
        tvPlaceDetailOpenNow.text = if (place.isOpen()) {
            "Открыто"
        } else {
            "Закрыто"
        }
        if (place.photos.isNotEmpty()) {
            ivPlaceDetailImage.load(getPhotoUrl(place.photos[0].photo_reference , 400 , 400))
        }

        if (place.reviews.isNotEmpty()) {
            reviewsAdapter.reviews = place.reviews
        } else {
            tvPlaceDetailReviewsLabel.visibility = View.GONE
            rvPlaceDetailReviews.visibility = View.GONE
        }
    }
}