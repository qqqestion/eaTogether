package ru.blackbull.eatogether.ui.main.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.BaseFragment
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        ibtnProfileSettings.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            )
        }

//        btnMore.setOnClickListener {
//            snackbar("Clicked")
//        }
//
        // TODO: когда будет статистика, добавить отображение загрузки
        viewModel.currentUser.observe(viewLifecycleOwner , EventObserver(
            onError = {
//                showErrorDialog(it)
            }
        ) { user ->
            ibtnProfileImage.load(user?.mainImageUri) {
                transformations(CircleCropTransformation())
            }
        })
        viewModel.statisticStatus.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
                Timber.d(it)
            }
        ) { statistic ->
            btnUniquePlaces.text = "${statistic.uniquePlaces} новых мест посещено"
            btnPartiesCount.text = "${statistic.partyEnded} завершенных компаний"
        })
        viewModel.getCurrentUser()
        viewModel.getStatistic()
    }
}
