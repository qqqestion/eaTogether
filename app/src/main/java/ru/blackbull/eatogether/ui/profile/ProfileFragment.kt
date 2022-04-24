package ru.blackbull.eatogether.ui.profile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2

@AndroidEntryPoint
class ProfileFragment : BaseFragmentV2<ProfileViewModel>(
    R.layout.fragment_profile,
    ProfileViewModel::class
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ibtnProfileSettings.setOnClickListener {
            viewModel.onProfileClicked()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::handleState)
            }
        }
    }

    private fun handleState(state: ProfileState) {
        state.image?.let { image ->
            ibtnProfileImage.load(image) {
                transformations(CircleCropTransformation())
            }
        }
        state.statistic?.let { statistics ->
            btnUniquePlaces.text = "${statistics.uniquePlaces} новых мест посещено"
            btnPartiesCount.text = "${statistics.partyEnded} завершенных компаний"
        }
    }
}
