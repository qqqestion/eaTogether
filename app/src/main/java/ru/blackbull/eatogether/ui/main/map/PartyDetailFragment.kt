package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_party_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyParticipantAdapter
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.other.EventObserver


@AndroidEntryPoint
class PartyDetailFragment : Fragment(R.layout.fragment_party_detail) {

    private lateinit var partyId: String

    private lateinit var partyParticipantAdapter: PartyParticipantAdapter

    private val partyDetailViewModel: PartyDetailViewModel by viewModels()

    private val args: PartyDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()

        partyId = args.partyId

        partyDetailViewModel.getPartyById(partyId)
    }

    private fun subscribeToObservers() {
        partyDetailViewModel.selectedParty.observe(viewLifecycleOwner , EventObserver { party ->
            updatePartyInfo(party)
            partyDetailViewModel.getPartyParticipants(party)
            partyDetailViewModel.getPlaceDetail(party.placeId!!)
        })
        partyDetailViewModel.partyParticipants.observe(
            viewLifecycleOwner ,
            EventObserver { participants ->
                partyParticipantAdapter.participants = participants
            })
        partyDetailViewModel.placeDetail.observe(viewLifecycleOwner , EventObserver { placeDetail ->
            updatePlaceInfo(placeDetail)
        })
    }

    private fun updatePlaceInfo(placeDetail: PlaceDetail) {
        tvPartyDetailPlaceName.text = placeDetail.name
        tvPartyDetailPlaceAddress.text = placeDetail.address
    }

    private fun updatePartyInfo(party: Party) {
        etPartyDetailTime.setText(party.time?.toDate().toString())
        etPartyDetailTime.inputType = InputType.TYPE_NULL
    }

    private fun setupRecyclerView() = rvPartyDetailParticipants.apply {
        partyParticipantAdapter = PartyParticipantAdapter()
        adapter = partyParticipantAdapter
        layoutManager = LinearLayoutManager(requireContext())

        partyParticipantAdapter.setOnUserClickListener {
            findNavController().navigate(
                PartyDetailFragmentDirections.actionPartyDetailFragmentToUserInfoFragment(it)
            )
        }
    }
}