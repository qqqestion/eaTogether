package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_party_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyParticipantAdapter
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.googleplaces.PlaceDetail
import ru.blackbull.eatogether.ui.InformationActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel

private const val KEY = "partyId"

class PartyDetailFragment : Fragment(R.layout.fragment_party_detail) {

    private lateinit var partyId: String

    private lateinit var adapter: PartyParticipantAdapter

    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        firebaseViewModel = (activity as InformationActivity).firebaseViewModel
        placeViewModel = (activity as InformationActivity).placeViewModel

        arguments?.let {
            partyId = it.getString(KEY).toString()
        }

        setupRecyclerView()

        firebaseViewModel.selectedParty.observe(viewLifecycleOwner , Observer { party ->
            updatePartyInfo(party)
            firebaseViewModel.getPartyParticipants(party)
        })
        firebaseViewModel.partyParticipants.observe(viewLifecycleOwner , Observer { participants ->
            adapter.differ.submitList(participants)
        })
        placeViewModel.placeDetail.observe(viewLifecycleOwner , Observer { placeDetail ->
            updatePlaceInfo(placeDetail)
        })
        firebaseViewModel.getPartyById(partyId)
    }

    private fun updatePlaceInfo(placeDetail: PlaceDetail) {
        tvPartyDetailPlaceName.text = placeDetail.name
        tvPartyDetailPlaceAddress.text = placeDetail.formatted_address
    }

    private fun updatePartyInfo(party: Party) {
        etPartyDetailTitle.setText(party.title)
        etPartyDetailTitle.inputType = InputType.TYPE_NULL
        etPartyDetailDescription.setText(party.description)
        etPartyDetailDescription.inputType = InputType.TYPE_NULL
        etPartyDetailTime.setText(party.time?.toDate().toString())
        etPartyDetailTime.inputType = InputType.TYPE_NULL
    }

    private fun setupRecyclerView() {
        adapter = PartyParticipantAdapter()
        rvParticipants.adapter = adapter
        rvParticipants.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        fun newInstance(partyId: String) = PartyDetailFragment().apply {
            arguments = Bundle().apply {
                putString(KEY , partyId)
            }
        }
    }
}