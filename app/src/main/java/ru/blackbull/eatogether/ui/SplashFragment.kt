package ru.blackbull.eatogether.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.UiStateWithData

@AndroidEntryPoint
class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        viewModel.isSignInStatus.observe(viewLifecycleOwner) {
            when (it) {
                is UiStateWithData.Failure -> {
                    findNavController().navigate(
                        SplashFragmentDirections.actionSplashFragmentToStartFragment()
                    )
                }
                UiStateWithData.Loading -> {
                    /* no-op */
                }
                is UiStateWithData.Success -> {
                    if (it.data) {
                        findNavController().navigate(
                            SplashFragmentDirections.actionSplashFragmentToMapFragment()
                        )
                    } else {
                        findNavController().navigate(
                            SplashFragmentDirections.actionSplashFragmentToStartFragment()
                        )
                    }
                }
            }
        }
    }
}