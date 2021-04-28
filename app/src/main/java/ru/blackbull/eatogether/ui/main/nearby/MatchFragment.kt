package ru.blackbull.eatogether.ui.main.nearby

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_match.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.snackbar


@AndroidEntryPoint
class MatchFragment : Fragment(R.layout.fragment_match) {

    private val viewModel: MatchViewModel by viewModels()
    private val args: MatchFragmentArgs by navArgs()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val matchedUser = args.matchedUser
        ivSecondUser.load(matchedUser.imageUri) {
            transformations(CircleCropTransformation())
        }

        viewModel.user.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            } ,
            onLoading = {

            }
        ) { currentUser ->
            ivFirstUser.load(currentUser.imageUri) {
                transformations(CircleCropTransformation())
            }
            tvMatchLabel.text =
                "У вас взаимный лайк с ${matchedUser.firstName} ${matchedUser.lastName}"
        })
        viewModel.getCurrentUser()
    }
}