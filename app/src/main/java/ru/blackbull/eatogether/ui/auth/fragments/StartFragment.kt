package ru.blackbull.eatogether.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_start.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.main.dialogs.ErrorDialog
import timber.log.Timber


@AndroidEntryPoint
class StartFragment : BaseFragment(R.layout.fragment_start) {

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        Timber.d("StartFragment")

        btnStart.setOnClickListener {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToRegistrationPhoneNumberFragment()
            )
        }
    }

}