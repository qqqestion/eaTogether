package ru.blackbull.eatogether.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_start.*
import ru.blackbull.eatogether.R

class StartFragment : Fragment(R.layout.fragment_start) {

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
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