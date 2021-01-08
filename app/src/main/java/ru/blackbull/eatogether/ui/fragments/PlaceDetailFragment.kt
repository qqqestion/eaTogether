package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.load
import kotlinx.android.synthetic.main.fragment_place_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.adapters.ReviewAdapter
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.ui.InformationActivity
import ru.blackbull.eatogether.ui.TempActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel
import ru.blackbull.eatogether.util.PlaceDataParser


private const val KEY = "place_id"

class PlaceDetailFragment : Fragment(R.layout.fragment_place_detail) , View.OnClickListener {

    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var firebaseViewModel: FirebaseViewModel

    private lateinit var placeId: String

    private lateinit var adapter: PartyAdapter

    private val TAG = "TagForDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity is InformationActivity) {
            placeViewModel = (activity as InformationActivity).placeViewModel
            firebaseViewModel = (activity as InformationActivity).firebaseViewModel
        } else {
            placeViewModel = (activity as TempActivity).placeViewModel
            firebaseViewModel = (activity as TempActivity).firebaseViewModel
        }

        arguments?.let {
            placeId = it.getString(KEY).toString()
        }
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        adapter = PartyAdapter()
        rvParties.adapter = adapter
        btnCreateParty.setOnClickListener(this)

        adapter.setOnItemViewClickListener { party ->
            (activity as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_for_fragments , PartyDetailFragment.newInstance(party.id!!))
                .addToBackStack(null)
                .commit()
        }
        adapter.setOnJoinCLickListener { party ->
            firebaseViewModel.addUserToParty(party)
            (activity as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_for_fragments , PartyDetailFragment.newInstance(party.id!!))
                .addToBackStack(null)
                .commit()
        }

        placeViewModel.placeDetail.observe(viewLifecycleOwner , Observer { placeDetail ->
            updatePlaceInfo(placeDetail)
        })
        firebaseViewModel.searchParties.observe(viewLifecycleOwner , Observer { parties ->
            Log.d(TAG , "parties: $parties")
            adapter.differ.submitList(parties)
        })
        placeViewModel.getPlaceDetail(placeId!!)
        firebaseViewModel.searchPartyByPlace(placeId)
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
                        tvPlaceName.text as String , address.text as String
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
        tvPlaceName.text = place.name
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
            rvReviews.adapter = ReviewAdapter(context!! , place.reviews!!)
        } else {
            reviews_label.visibility = View.GONE
            rvReviews.visibility = View.GONE
        }
    }
}