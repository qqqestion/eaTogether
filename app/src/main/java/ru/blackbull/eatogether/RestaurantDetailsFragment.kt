package ru.blackbull.eatogether

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val KEY = "place_id"

class RestaurantDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var placeID: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeID = it.getString(KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parser = PlaceDataParser()
        val view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false)

        GlobalScope.launch(Dispatchers.Main) {
            val placeData = parser.getPlaceDetail(placeID!!)
            view.findViewById<TextView>(R.id.name).text = placeData.name
            view.findViewById<TextView>(R.id.address).text = placeData.formatted_address
            view.findViewById<TextView>(R.id.phone).text = placeData.formatted_phone_number
            if (placeData.opening_hours != null && placeData.opening_hours!!.open_now) {
                view.findViewById<TextView>(R.id.open_now).text = "Open"
            } else {
                view.findViewById<TextView>(R.id.open_now).text = "Close"
            }
            if (placeData.photos != null) {
                Picasso.with(view.context)
                    .load(parser.getPhotoUrl(placeData.photos!![0].photo_reference, 400, 400))
                    .into(view.findViewById<ImageView>(R.id.image))
            }

            val reviewRecyclerView = view.findViewById<RecyclerView>(R.id.review_list)
            Log.d("recyclerView", placeData.reviews.toString())
            reviewRecyclerView.adapter = ReviewAdapter(context!!, placeData.reviews!!)
        }
        return view
    }

    companion object {
        fun newInstance(placeID: String) =
            RestaurantDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY, placeID)
                }
            }
    }
}