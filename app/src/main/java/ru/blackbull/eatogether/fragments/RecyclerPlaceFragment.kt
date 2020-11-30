package ru.blackbull.eatogether.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.googleplacesapi.BasicLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.blackbull.eatogether.utils.PlaceDataParser
import ru.blackbull.eatogether.adapters.PlaceListAdapter


private const val LATITUDE = "lat"
private const val LONGITUDE = "lng"

class RecycleRestaurantsFragment : Fragment() {
    private val TAG: String = "TagForDebug"
    private var lat: Double? = null
    private var lng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lat = it.getDouble(LATITUDE)
            lng = it.getDouble(LONGITUDE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_place, container, false)
        val position = LatLng(lat!!, lng!!)
        GlobalScope.launch(Dispatchers.Main) {
            val result = getData(position)
            createRecycleView(result, view)
        }
        return view
    }

    private fun createRecycleView(data: List<BasicLocation>, view: View) {
        val adapter = PlaceListAdapter(view.context, data)
        val list = view.findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(view.context)
    }

    private suspend fun getData(latLng: LatLng): List<BasicLocation> {
        return withContext(Dispatchers.IO) {
            val parser = PlaceDataParser()
            return@withContext parser.getNearByPlaces(latLng)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(lat: Double, lng: Double) =
            RecycleRestaurantsFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE, lat)
                    putDouble(LONGITUDE, lng)
                }
            }
    }
}