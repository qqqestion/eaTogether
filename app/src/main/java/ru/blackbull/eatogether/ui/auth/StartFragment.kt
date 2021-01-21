package ru.blackbull.eatogether.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_start.*
import ru.blackbull.eatogether.R

class StartFragment : Fragment(R.layout.fragment_start) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        if (authViewModel.isAuthenticated()) {
            findNavController().navigate(
                R.id.action_startFragment_to_mapFragment,
                savedInstanceState
            )
        }

        btnStartLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_startFragment_to_loginFragment
            )
        }
        btnStartRegistration.setOnClickListener {
            findNavController().navigate(
                R.id.action_startFragment_to_registrationFragment
            )
        }
    }
}