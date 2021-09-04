package ru.blackbull.eatogether.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.blackbull.data.models.firebase.toUser
import ru.blackbull.eatogether.adapters.InviteUserForLunchAdapter
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.InviteForLunchViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class InviteForLunchDialogFragment(
    private val partyId: String
) : DialogFragment() {

    private var dialogView: View? = null

    @Inject
    lateinit var invitationAdapter: InviteUserForLunchAdapter

    private val viewModel: InviteForLunchViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = invitationAdapter
        }
        Timber.d("onCreateDialog")

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Пригласить друзей")
            .setView(dialogView)
            .setPositiveButton("Подтвердить") { _ , _ ->
            }
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        return dialogView
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        viewModel.getFriendList(partyId)
        invitationAdapter.setOnUserClickListener {
            viewModel.sendInvitation(partyId , it)
        }

        viewModel.invitationStatus.observe(viewLifecycleOwner , EventObserver {
            invitationAdapter.users -= it
        })

        viewModel.friendList.observe(viewLifecycleOwner , EventObserver { users ->
            invitationAdapter.users = users.map { it.toUser() }
        })
    }
}