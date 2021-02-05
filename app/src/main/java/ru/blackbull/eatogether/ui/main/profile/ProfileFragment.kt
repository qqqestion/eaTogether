package ru.blackbull.eatogether.ui.main.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.main.snackbar

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        ibtnProfileSettings.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            )
        }

        viewModel.currentUser.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            } ,
            onLoading = {

            }
        ) { user ->
            ibtnProfileImage.load(user?.imageUri) {
                transformations(CircleCropTransformation())
            }
        })
        viewModel.getCurrentUser()
    }
}

