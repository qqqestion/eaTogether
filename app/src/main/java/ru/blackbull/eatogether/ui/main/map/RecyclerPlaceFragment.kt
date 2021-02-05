package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recycler_place.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PlaceListAdapter

@AndroidEntryPoint
class RecycleRestaurantsFragment : Fragment(R.layout.fragment_recycler_place) {

    val args: RecycleRestaurantsFragmentArgs by navArgs()
    private val mapViewModel: MapViewModel by viewModels()

    private lateinit var placeAdapter: PlaceListAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()
        setupRecyclerView()
        val location = args.location
        mapViewModel.getNearbyPlaces(location.latitude , location.longitude)
        placeAdapter.setOnItemClickListener { location ->
            val bundle = Bundle().apply {
                putString("placeId" , location.placeId)
            }
            findNavController().navigate(
                R.id.action_recycleRestaurantsFragment_to_placeDetailFragment ,
                bundle
            )
        }
    }

    private fun subscribeToObservers() {
        mapViewModel.nearbyPlaces.observe(viewLifecycleOwner , Observer { places ->
            placeAdapter.places = places
        })
    }

    private fun setupRecyclerView() {
        placeAdapter = PlaceListAdapter()
        rvNearbyPlaces.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}