package ru.blackbull.eatogether.ui.place_search_result

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2

@AndroidEntryPoint
class SearchResultFragment : BaseFragmentV2<SearchResultViewModel>(
    R.layout.fragment_search_result, SearchResultViewModel::class
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeAdapter = PlaceAdapter()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    placeAdapter.places = state.places
                }
            }
        }

        rvNearbyPlaces.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        placeAdapter.setOnItemClickListener { place ->
            viewModel.onItemClick(place)
        }
    }
}