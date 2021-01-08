package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.fragment_match.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.ui.NearbyActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel


private const val KEY = "matchedUserId"

class MatchFragment : Fragment(R.layout.fragment_match) {

    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var matchedUser: User

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        firebaseViewModel = (activity as NearbyActivity).firebaseViewModel

        arguments?.let {
            matchedUser = it.getSerializable(KEY) as User
            ivSecondUser.load(matchedUser._imageUri) {
                transformations(CircleCropTransformation())
            }
        }

        firebaseViewModel.user.observe(viewLifecycleOwner , Observer { currentUser ->
            ivFirstUser.load(currentUser._imageUri) {
                transformations(CircleCropTransformation())
            }
            tvMatchLabel.text =
                "У вас взаимный лайк с ${matchedUser.firstName} ${matchedUser.lastName}"
        })
        firebaseViewModel.getCurrentUser()
    }

    companion object {
        fun newInstance(matchedUser: User) = MatchFragment().apply {
            arguments = Bundle().apply {
                putSerializable(KEY , matchedUser)
            }
        }
    }
}