package ru.blackbull.eatogether.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_create_party.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.Resource


@AndroidEntryPoint
class CreatePartyFragment : Fragment(R.layout.fragment_create_party) {

    private val createPartyViewModel: CreatePartyViewModel by viewModels()
    private val args: CreatePartyFragmentArgs by navArgs()

    private lateinit var placeUri: String

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()

        placeUri = args.placeUri
        tvCreatePartyPlaceName.text = args.placeName
        tvCreatePartyPlaceAddress.text = args.placeAddress

        btnCreatePartyConfirm.setOnClickListener {
            createPartyViewModel.createParty(
                title = etCreatePartyPlaceTitle.text.toString() ,
                description = etCreatePartyPlaceDescription.text.toString() ,
                date = etCreatePartyPickDate.text.toString() ,
                time = etCreatePartyPickTime.text.toString() ,
                placeId = placeUri
            )
        }
        btnCreatePartyCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun subscribeToObservers() {
        createPartyViewModel.createPartyResult.observe(
            viewLifecycleOwner ,
            Observer { result ->
                when (result) {
                    is Resource.Success -> {
                        Snackbar.make(
                            requireActivity().rootLayout ,
                            getString(R.string.success_party_created) ,
                            Snackbar.LENGTH_LONG
                        ).show()
                        btnCreatePartyConfirm.isEnabled = true
                        findNavController().popBackStack()
                    }
                    is Resource.Error -> {
                        val msg = result.msg ?: getString(R.string.errormessage_unknown_error)
                        Snackbar.make(
                            requireView() ,
                            msg ,
                            Snackbar.LENGTH_LONG
                        ).show()
                        btnCreatePartyConfirm.isEnabled = true
                    }
                    is Resource.Loading -> {
                        btnCreatePartyConfirm.isEnabled = false
                    }
                }
            })
    }
}