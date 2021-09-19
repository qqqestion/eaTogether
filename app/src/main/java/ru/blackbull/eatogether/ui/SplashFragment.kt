package ru.blackbull.eatogether.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.UiStateWithData

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        viewModel.accountInfoSetStatus.observe(viewLifecycleOwner) {
            when (it) {
                is UiStateWithData.Failure -> {
                }
                UiStateWithData.Loading -> TODO()
                is UiStateWithData.Success -> {
                    if (it.data) {

                    } else {

                    }
                }
            }
        }
    }
}