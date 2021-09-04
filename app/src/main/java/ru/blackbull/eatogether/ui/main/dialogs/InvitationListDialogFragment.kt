package ru.blackbull.eatogether.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.blackbull.eatogether.adapters.InvitationAdapter
import ru.blackbull.data.models.firebase.InvitationWithUsers
import ru.blackbull.data.models.firebase.toInvitationWithUsers
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.friends.FriendsViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class InvitationListDialogFragment : DialogFragment() {

    private var dialogView: View? = null

    @Inject
    lateinit var invitationAdapter: InvitationAdapter

    private val viewModel: FriendsViewModel by activityViewModels()

    private var onAddToFriendListClickListener: ((InvitationWithUsers) -> Unit)? = null

    fun setOnAddToFriendListClickListener(listener: (InvitationWithUsers) -> Unit) {
        onAddToFriendListClickListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = invitationAdapter
        }
        Timber.d("onCreateDialog")

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Заявки в друзья")
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
        viewModel.getInvitationList()
        Timber.d("onViewCreated")

        invitationAdapter.setOnAddToFriendListClickListener(onAddToFriendListClickListener)

        viewModel.addToFriendList.observe(viewLifecycleOwner , EventObserver {
            invitationAdapter.invitations -= it
        })

        viewModel.invitationList.observe(viewLifecycleOwner , EventObserver { invitations ->
            Timber.d("Invitations: $invitations")
            invitationAdapter.invitations = invitations.map { it.toInvitationWithUsers() }
        })
    }
}