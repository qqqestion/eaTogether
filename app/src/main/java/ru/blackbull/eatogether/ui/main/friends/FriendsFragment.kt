package ru.blackbull.eatogether.ui.main.friends

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_friends.*
import ru.blackbull.data.models.firebase.toUser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyParticipantAdapter
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.core.BaseFragment
import ru.blackbull.eatogether.ui.main.dialogs.InvitationListDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment : BaseFragment(R.layout.fragment_friends) {

    private val viewModel: FriendsViewModel by activityViewModels()

    @Inject
    lateinit var userAdapter: PartyParticipantAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        viewModel.getFriendList()

        rvFriends.adapter = userAdapter
        rvFriends.layoutManager = LinearLayoutManager(requireContext())

        userAdapter.setOnUserClickListener {
            findNavController().navigate(
                FriendsFragmentDirections.actionFriendsFragmentToUserInfoFragment(it)
            )
        }

        fabInvitation.setOnClickListener {
            InvitationListDialogFragment().apply {
                setOnAddToFriendListClickListener {
                    viewModel.addToFriendList(it)
                }
            }.show(childFragmentManager , null)
        }

        viewModel.addToFriendList.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            }
        ) {
            userAdapter.participants += it.inviter!!
        })

        viewModel.friendList.observe(viewLifecycleOwner , EventObserver(
            onError = {
                hideLoadingBar()
                showErrorDialog(it)
            } ,
            onLoading = {
                showLoadingBar()
            }
        ) { users ->
            hideLoadingBar()
            userAdapter.participants = users.map { it.toUser() }
        })
    }
}