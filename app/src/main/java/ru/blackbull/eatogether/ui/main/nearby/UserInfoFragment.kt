package ru.blackbull.eatogether.ui.main.nearby

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user_info.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.ImageAdapter
import ru.blackbull.eatogether.models.firebase.FriendState
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class UserInfoFragment : BaseFragment(R.layout.fragment_user_info) {

    private val args: UserInfoFragmentArgs by navArgs()

    private val viewModel: UserInfoViewModel by viewModels()

    @Inject
    lateinit var imageAdapter: ImageAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        imageAdapter.images = args.user.images.map { Uri.parse(it) }
        viewPager.adapter = imageAdapter
        tvFullName.text = args.user.fullName()
        tvDescription.text = args.user.description

        viewModel.checkUserStatus(args.user)

        btnAddToFriendList.setOnClickListener {
            viewModel.addToFriendList(args.user)
        }

        viewModel.addToFriendListStatus.observe(viewLifecycleOwner , EventObserver(
            onError = {
                hideLoadingBar()
                showErrorDialog(it)
            } ,
            onLoading = {
                showLoadingBar()
            }
        ) { friendState ->
            hideLoadingBar()
            btnAddToFriendList.isVisible = false
            btnInvitationSent.isVisible = false
            btnAlreadyFriend.isVisible = false
            when (friendState) {
                FriendState.INVITATION_SENT -> btnInvitationSent.isVisible = true
                FriendState.UNFRIEND -> btnAddToFriendList.isVisible = true
                FriendState.FRIEND -> btnAlreadyFriend.isVisible = true
                FriendState.ITSELF -> Unit
            }
        })
        viewModel.userStatus.observe(viewLifecycleOwner , EventObserver(
            onError = {
                showErrorDialog(it)
            }
        ) { friendState ->
            when (friendState) {
                FriendState.INVITATION_SENT -> btnInvitationSent.isVisible = true
                FriendState.UNFRIEND -> btnAddToFriendList.isVisible = true
                FriendState.FRIEND -> btnAlreadyFriend.isVisible = true
                FriendState.ITSELF -> Unit
            }
        })
    }
}