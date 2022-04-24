package ru.blackbull.eatogether.ui.splash

import dagger.hilt.android.AndroidEntryPoint
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2

@AndroidEntryPoint
class SplashFragment : BaseFragmentV2<SplashViewModel>(
    R.layout.fragment_splash, SplashViewModel::class
)