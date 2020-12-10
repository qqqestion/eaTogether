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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.blackbull.eatogether.utils.PlaceDataParser
import ru.blackbull.eatogether.adapters.PlaceListAdapter
import ru.blackbull.eatogether.googleplacesapi.ResultList
import ru.blackbull.eatogether.modules.NetworkModule


private const val LATITUDE = "lat"
private const val LONGITUDE = "lng"

class RecycleRestaurantsFragment : Fragment() {
    private val TAG: String = "TagForDebug"
    private var lat: Double? = null
    private var lng: Double? = null

    private lateinit var rvPlaces: RecyclerView

    private val theGooglePlaceApiService = NetworkModule.theGooglePlaceApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        rvPlaces = layout.findViewById(R.id.rv_places)
        val position = LatLng(lat!! , lng!!)
        val responseCall = theGooglePlaceApiService.getNearbyPlaces("$lat,$lng")
        responseCall.enqueue(object : Callback<ResultList> {
            override fun onResponse(call: Call<ResultList> , response: Response<ResultList>) {
                val responseResult = response.body()
                if (responseResult?.status == "OK") {
                    Log.d(
                        "DebugAPI" ,
                        "Retrofit -> onResponse: success: ${responseResult.placeList}"
                    )
                    setListForRecyclerViewAdapter(responseResult.placeList)
                } else {
                    Log.d(
                        "DebugAPI" ,
                        "Retrofit -> onResponse: failed: ${responseResult?.errorMessage}"
                    )
                }
            }

            override fun onFailure(call: Call<ResultList> , t: Throwable) {
                t.printStackTrace()
                Log.d("DebugAPI" , "Retrofit -> onFailure: ${t.message}")
            }
        })

        return layout
    }

    private fun setListForRecyclerViewAdapter(data: List<BasicLocation>) {
        val adapter = PlaceListAdapter(context!! , data)
        rvPlaces.adapter = adapter
        rvPlaces.layoutManager = LinearLayoutManager(context)
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