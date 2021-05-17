package ru.blackbull.eatogether.ui.main.map

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_party_detail.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyParticipantAdapter
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.dialogs.InviteForLunchDialogFragment
import java.util.*


@AndroidEntryPoint
class PartyDetailFragment : Fragment(R.layout.fragment_party_detail) {

    private lateinit var partyId: String

    private lateinit var partyParticipantAdapter: PartyParticipantAdapter

    private val viewModel: PartyDetailViewModel by viewModels()

    private val args: PartyDetailFragmentArgs by navArgs()

    private lateinit var party: Party

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()

        partyId = args.partyId

        viewModel.getPartyById(partyId)

        tvTime.setOnClickListener {
            pickDateTime()
        }
        btnLeaveParty.setOnClickListener {
            viewModel.leaveParty(party)
        }
        btnInviteFriends.setOnClickListener {
            InviteForLunchDialogFragment(partyId).show(childFragmentManager , null)
        }
        btnJoinParty.setOnClickListener {
            viewModel.addUserToParty(party)
        }
    }

    private fun pickDateTime() {
        val partyTime = Calendar.getInstance()
        partyTime.timeInMillis = party.time!!.toDate().time
        val startYear = partyTime.get(Calendar.YEAR)
        val startMonth = partyTime.get(Calendar.MONTH)
        val startDay = partyTime.get(Calendar.DAY_OF_MONTH)
        val startHour = partyTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = partyTime.get(Calendar.MINUTE)

        TimePickerDialog(requireContext() , { _ , hour , minute ->
            partyTime.set(startYear , startMonth , startDay , hour , minute)
            setDateTimeInTextView(partyTime)
            party.time = Timestamp(partyTime.time)
            viewModel.updateParty(party)
        } , startHour , startMinute , true).show()
    }

    private fun setDateTimeInTextView(pickedDateTime: Calendar) {
        tvTime.text = DateUtils.formatDateTime(
            requireContext() ,
            pickedDateTime.timeInMillis ,
            DateUtils.FORMAT_SHOW_TIME
        )
    }

    private fun subscribeToObservers() {
        viewModel.addUserStatus.observe(viewLifecycleOwner , EventObserver { user ->
            partyParticipantAdapter.participants += user
            btnJoinParty.isVisible = false
            btnInviteFriends.isVisible = true
            btnLeaveParty.isVisible = true
        })
        viewModel.selectedParty.observe(viewLifecycleOwner , EventObserver { party ->
            this.party = party
            updatePartyInfo(party)
            viewModel.getPartyParticipants(party)
            viewModel.getPlaceDetail(party.placeId!!)
        })
        viewModel.partyParticipants.observe(
            viewLifecycleOwner ,
            EventObserver { participants ->
                partyParticipantAdapter.participants = participants
            })
        viewModel.placeDetail.observe(viewLifecycleOwner , EventObserver { placeDetail ->
            updatePlaceInfo(placeDetail)
        })
        viewModel.leavePartyStatus.observe(viewLifecycleOwner , EventObserver {
            findNavController().popBackStack()
        })
    }

    private fun updatePlaceInfo(placeDetail: PlaceDetail) {
        tvPartyDetailPlaceName.text = placeDetail.name
        tvAddress.text = placeDetail.address
    }

    private fun updatePartyInfo(party: Party) {
        val partyTime = Calendar.getInstance()
        partyTime.timeInMillis = party.time!!.toDate().time
        tvTime.text = DateUtils.formatDateTime(
            requireContext() ,
            party.time?.toDate()!!.time ,
            DateUtils.FORMAT_SHOW_TIME
        )
        if (party.isCurrentUserInParty) {
            btnJoinParty.isVisible = false
            btnInviteFriends.isVisible = true
            btnLeaveParty.isVisible = true
        } else {
            btnJoinParty.isVisible = true
            btnInviteFriends.isVisible = false
            btnLeaveParty.isVisible = false
        }
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