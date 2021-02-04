package ru.blackbull.eatogether.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_start.*
import ru.blackbull.eatogether.R

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {

    val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
//        authViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(AuthViewModel::class.java)

        if (authViewModel.isAuthenticated()) {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToMapFragment()
            )
        }

        btnStartLogin.setOnClickListener {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToLoginFragment()
            )
        }
        btnStartRegistration.setOnClickListener {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToRegistrationFragment()
            )
        }
    }
}