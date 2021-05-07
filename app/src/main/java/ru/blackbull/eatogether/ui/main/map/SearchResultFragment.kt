package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search_result.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PlaceAdapter
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.snackbar

@AndroidEntryPoint
class SearchResultFragment : Fragment(R.layout.fragment_search_result) {

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var placeAdapter: PlaceAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()
        setupRecyclerView()
//        val location = args.location
//        location?.let {
//            // TODO: сделать поиск ближайших мест
////            viewModel.getNearbyPlaces(it.latitude , it.longitude)
//        }
        placeAdapter.setOnItemClickListener { place ->
            findNavController().navigate(
                SearchResultFragmentDirections.actionSearchResultToPlaceDetailFragment(
                    place
                )
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            } ,
            onLoading = {

            }
        ) { places ->
            placeAdapter.places = places
        })
    }

    private fun setupRecyclerView() {
        placeAdapter = PlaceAdapter()
        rvNearbyPlaces.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}