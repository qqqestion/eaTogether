package ru.blackbull.eatogether.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.blackbull.eatogether.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val firebaseViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        ibtnProfileSettings.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            )
        }

        firebaseViewModel.currentUser.observe(viewLifecycleOwner , Observer { user ->
            if (user == null) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToStartFragment()
                )
            }
            ibtnProfileImage.load(user?.imageUri) {
                transformations(CircleCropTransformation())
            }
        })

    }

    override fun onStart() {
        super.onStart()
        firebaseViewModel.getCurrentUser()
    }
}

