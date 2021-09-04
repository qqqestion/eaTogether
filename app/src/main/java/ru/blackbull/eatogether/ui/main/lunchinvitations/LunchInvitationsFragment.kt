package ru.blackbull.eatogether.ui.main.lunchinvitations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_lunch_invitations.*
import ru.blackbull.data.models.firebase.toLunchInvitationWithUsers
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.LunchInvitationAdapter
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class LunchInvitationsFragment : BaseFragment(R.layout.fragment_lunch_invitations) {

    private val viewModel: LunchInvitationsViewModel by viewModels()

    @Inject
    lateinit var lunchInvitationsAdapter: LunchInvitationAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        rvInvitations.apply {
            adapter = lunchInvitationsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getLunchInvitations()

        viewModel.lunchInvitations.observe(viewLifecycleOwner , EventObserver { invitations ->
            lunchInvitationsAdapter.invitations = invitations.map { it.toLunchInvitationWithUsers() }
        })

        lunchInvitationsAdapter.setOnViewPartyClickListener {
            findNavController().navigate(
                LunchInvitationsFragmentDirections.actionLunchInvitationsFragmentToPartyDetailFragment(it)
            )
        }
    }
}