package ru.blackbull.eatogether.ui.nearby

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.fragment_match.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel


class MatchFragment : Fragment(R.layout.fragment_match) {

    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private val args: MatchFragmentArgs by navArgs()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val matchedUser = args.matchedUser
        ivSecondUser.load(matchedUser._imageUri) {
            transformations(CircleCropTransformation())
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
}