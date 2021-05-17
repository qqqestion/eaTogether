package ru.blackbull.eatogether.ui.main.lunchinvitations

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment

@AndroidEntryPoint
class LunchInvitationsFragment : BaseFragment(R.layout.fragment_lunch_invitations) {

    private val viewModel: LunchInvitationsViewModel by viewModels()


}