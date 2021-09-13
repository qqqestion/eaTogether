package ru.blackbull.eatogether.ui.auth.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_set_account_info.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.SetAccountInfoViewModel
import ru.blackbull.eatogether.ui.auth.UiState
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class SetAccountInfoFragment : BaseFragment(R.layout.fragment_set_account_info) {

    private val viewModel: SetAccountInfoViewModel by viewModels()

    private val selectedDate = Calendar.getInstance()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        subscribeToObservers()

        etRegistrationBirthday.setOnClickListener {
            snackbar("Android version is too low")
            Timber.d("Android version is too low")
            displayDatePickerDialog()
        }

        btnRegistrationConfirm.setOnClickListener {
            submitAccountInfo()
        }
    }

    private fun submitAccountInfo() {
        viewModel.submitAccountInfo(
            etRegistrationFirstName.text.toString() ,
            etRegistrationLastName.text.toString() ,
            etRegistrationDescription.text.toString() ,
            selectedDate.time.time ,
            ""
        )
    }

    private fun displayDatePickerDialog() {
        val currentDateTime = Calendar.getInstance()

        val startYear = currentDateTime.get(Calendar.YEAR) - 18
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext() ,
            { _ , year , month , day ->
                selectedDate.set(year , month , day)
                etRegistrationBirthday.setText(
                    DateUtils.formatDateTime(
                        requireContext() ,
                        selectedDate.timeInMillis ,
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                    )
                )
            } ,
            startYear ,
            startMonth ,
            startDay).show()
    }

    private fun subscribeToObservers() {
        viewModel.setAccountInfoStatus.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                    hideLoadingBar()
                    snackbar(it.messageId)
                }
                UiState.Loading -> showLoadingBar()
                UiState.Success -> {
                    hideLoadingBar()
                    startActivity(Intent(requireActivity() , MainActivity::class.java))
                }
            }
        }
    }
}