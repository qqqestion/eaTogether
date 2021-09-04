package ru.blackbull.eatogether.ui.main.map

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateUtils
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
import ru.blackbull.domain.Resource
import java.util.*


@AndroidEntryPoint
class CreatePartyFragment : Fragment(R.layout.fragment_create_party) {

    private val createPartyViewModel: CreatePartyViewModel by viewModels()
    private val args: CreatePartyFragmentArgs by navArgs()

    private lateinit var placeId: String

    private val selectedDate = Calendar.getInstance()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()
        setDateTimeInTextView(selectedDate)

        placeId = args.placeUri
        tvAddress.text = args.placeAddress
        tvTime.setOnClickListener {
            pickDateTime()
        }


        btnCreatePartyConfirm.setOnClickListener {
            createPartyViewModel.createParty(
                datetime = selectedDate.time ,
                placeId = placeId
            )
        }
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

//        DatePickerDialog(requireContext() , { _ , year , month , day ->
        TimePickerDialog(requireContext() , { _ , hour , minute ->
            selectedDate.set(startYear , startMonth , startDay , hour , minute)
            setDateTimeInTextView(selectedDate)
        } , startHour , startMinute , true).show()
//        } , startYear , startMonth , startDay).show()
    }

    private fun setDateTimeInTextView(pickedDateTime: Calendar) {
        tvTime.text = DateUtils.formatDateTime(
            requireContext() ,
            pickedDateTime.timeInMillis ,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                    or DateUtils.FORMAT_SHOW_TIME
        )
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