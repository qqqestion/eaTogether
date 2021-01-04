package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import ru.blackbull.eatogether.util.PlaceDataParser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.adapters.ReviewAdapter
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel
import java.lang.RuntimeException
import coil.load
import kotlinx.android.synthetic.main.fragment_place_detail.*
import ru.blackbull.eatogether.ui.InformationActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel


private const val KEY = "place_id"

class PlaceDetailFragment : Fragment() , View.OnClickListener {

    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var firebaseViewModel: FirebaseViewModel

    private lateinit var placeId: String

    private lateinit var adapter: PartyAdapter

    private val TAG = "TagForDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placeViewModel = (activity as InformationActivity).placeViewModel
        firebaseViewModel = (activity as InformationActivity).firebaseViewModel

        arguments?.let {
            placeId = it.getString(KEY).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(
            R.layout.fragment_place_detail ,
            container , false
        )

        placeViewModel.placeDetail.observe(viewLifecycleOwner , Observer { placeDetail ->
            updatePlaceInfo(placeDetail)
        })
        placeViewModel.getPlaceDetail(placeId!!)

        adapter = PartyAdapter()
        firebaseViewModel.searchParties.observe(viewLifecycleOwner , Observer { parties ->
            Log.d(TAG , "parties: $parties")
            adapter.differ.submitList(parties)
        })
        firebaseViewModel.searchPartyByPlace(placeId)

        val partyRecyclerView: RecyclerView = layout.findViewById(R.id.rv_parties)

//        adapter = PartyListAdapter(context!! , partyList!!)
        partyRecyclerView.adapter = adapter

        layout.findViewById<Button>(R.id.create_party).setOnClickListener(this)

        return layout
    }

    companion object {
        fun newInstance(placeID: String) =
            PlaceDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY , placeID)
                }
            }
    }

    override fun onClick(p0: View?) {
        if (context is AppCompatActivity) {
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.layout_for_fragments ,
                    CreatePartyFragment.newInstance(
                        placeId ,
                        name.text as String , address.text as String
                    )
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updatePlaceInfo(place: PlaceDetail?) {
        if (place == null) {
            throw RuntimeException("cannot get place information")
        }
        name.text = place.name
        address.text = place.formatted_address
        phone.text = place.formatted_phone_number
        open_now.text = if (place.getIsOpen() == true) {
            "Открыто"
        } else {
            "Закрыто"
        }
        val parser = PlaceDataParser()
        if (place.photos != null) {
            image.load(parser.getPhotoUrl(place.photos!![0].photo_reference , 400 , 400))
        }

        if (place.reviews != null) {
            rv_reviews.adapter = ReviewAdapter(context!! , place.reviews!!)
        } else {
            reviews_label.visibility = View.GONE
            rv_reviews.visibility = View.GONE
        }
    }
}