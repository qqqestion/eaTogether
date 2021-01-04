package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recycler_place.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PlaceListAdapter
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.ui.InformationActivity
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel


private const val LATITUDE = "lat"
private const val LONGITUDE = "lng"

class RecycleRestaurantsFragment : Fragment() {
    private val TAG: String = "TagForDebug"
    private var lat: Double? = null
    private var lng: Double? = null

    private lateinit var placeViewModel: PlaceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placeViewModel = (activity as InformationActivity).placeViewModel
        arguments?.let {
            lat = it.getDouble(LATITUDE)
            lng = it.getDouble(LONGITUDE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_recycler_place , container , false)
        placeViewModel.nearbyPlaces.observe(viewLifecycleOwner , Observer { places ->
            setListForRecyclerViewAdapter(places)
        })
        placeViewModel.getNearbyPlaces(lat!! , lng!!)

        return layout
    }

    private fun setListForRecyclerViewAdapter(data: List<BasicLocation>) {
        val adapter = PlaceListAdapter(context!! , data)
        rv_places.adapter = adapter
        rv_places.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        @JvmStatic
        fun newInstance(lat: Double , lng: Double) =
            RecycleRestaurantsFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE , lat)
                    putDouble(LONGITUDE , lng)
                }
            }
    }
}