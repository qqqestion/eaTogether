package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_place_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.adapters.ReviewAdapter
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber


@AndroidEntryPoint
class PlaceDetailFragment : Fragment(R.layout.fragment_place_detail) , Session.SearchListener {

    private val args: PlaceDetailFragmentArgs by navArgs()

    private val placeDetailViewModel: PlaceDetailViewModel by viewModels()

    private lateinit var placeUri: String

    private lateinit var partiesAdapter: PartyAdapter
    private lateinit var reviewsAdapter: ReviewAdapter

    private lateinit var searchManager: SearchManager
    private lateinit var searchSession: Session

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        placeUri = args.placeUri
        searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )
        searchSession = searchManager.resolveURI(
            placeUri ,
            SearchOptions().apply {
                snippets = Snippet.BUSINESS_RATING1X.value
            } ,
            this
        )

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

//        placeDetailViewModel.getPlaceDetail(placeUri)
        placeDetailViewModel.searchPartyByPlace(placeUri)
    }

    private fun setupRecyclerView() {
        partiesAdapter = PartyAdapter()
        rvPlaceDetailParties.apply {
            adapter = partiesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

//        reviewsAdapter = ReviewAdapter()
//        rvPlaceDetailReviews.apply {
//            adapter = reviewsAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//        }
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
        tvPlaceDetailCategories.text = place.formatted_address
        tvPlaceDetailPhone.text = place.formatted_phone_number
        tvPlaceDetailWorkingHours.text = if (place.isOpen()) {
            "Открыто"
        } else {
            "Закрыто"
        }
    }

    override fun onSearchResponse(response: Response) {
        for (searchResult in response.collection.children) {
            val obj = searchResult.obj!!
            val businessMetadata = obj.metadataContainer.getItem(BusinessObjectMetadata::class.java)
            val ratingMetadata =
                obj.metadataContainer.getItem(BusinessRating1xObjectMetadata::class.java)
            val score = ratingMetadata?.score
            val ratings = ratingMetadata?.ratings
            val name = businessMetadata.name
            val address = businessMetadata.address.formattedAddress
            val phones = businessMetadata.phones.map { it.formattedNumber }
            val workingHours = businessMetadata.workingHours?.state?.text
            val categories = businessMetadata.categories.map { it.name }
            val bar = businessMetadata.features.map { it.id to it.value }

            tvPlaceDetailName.text = name
            tvAddress.text = address
            tvPlaceDetailCategories.text = categories.joinToString()
            tvPlaceDetailPhone.text = phones.firstOrNull()
            tvWorkingHours.text = workingHours
            tvScore.text = score.toString()
            // TODO: добавить работу с локализацией
            tvRatings.text = "$ratings оценок"

            Timber.d("Score: ${score}")
            Timber.d("Object name: ${obj.name}")
        }
    }

    override fun onSearchError(error: Error) {
        Timber.d("Yandex search error: $error")
        val errorMessage = when (error) {
            is RemoteError -> "Remote error"
            is NetworkError -> "Network error"
            else -> "Unknown error"
        }
        snackbar(errorMessage)
    }
}