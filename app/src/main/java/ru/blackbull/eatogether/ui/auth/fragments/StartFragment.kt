package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_start.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import timber.log.Timber


@AndroidEntryPoint
class StartFragment : BaseFragment(R.layout.fragment_start) {

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        btnStart.setOnClickListener {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToCreateAccountFragment()
            )
        }
        btnLogin.setOnClickListener {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToLoginFragment()
            )
        }
    }
}