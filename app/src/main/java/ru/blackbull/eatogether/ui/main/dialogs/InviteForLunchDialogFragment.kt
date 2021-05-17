package ru.blackbull.eatogether.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.blackbull.eatogether.adapters.InviteUserForLunchAdapter
import ru.blackbull.eatogether.ui.main.map.PartyDetailViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class InviteForLunchDialogFragment(
    private val partyId: String
) : DialogFragment() {

    private var dialogView: View? = null

    @Inject
    lateinit var invitationAdapter: InviteUserForLunchAdapter

    private val viewModel: PartyDetailViewModel by viewModels()

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

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

    }
}