package ru.blackbull.eatogether.ui.auth.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_set_account_info.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.ui.auth.CompleteRegistrationState
import ru.blackbull.eatogether.ui.auth.CompleteRegistrationViewModel
import ru.blackbull.eatogether.ui.trimmedText
import java.util.*

@AndroidEntryPoint
class CompleteRegistrationFragment : BaseFragmentV2<CompleteRegistrationViewModel>(
    R.layout.fragment_set_account_info, CompleteRegistrationViewModel::class
) {

    private val selectedDate = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            val isLoading = (state is CompleteRegistrationState.Loading)

            btnRegistrationConfirm.isEnabled = isLoading.not()
            progressBar.isVisible = isLoading
            when (state) {
                is CompleteRegistrationState.Error -> {
                    Log.d("!!!", "Error: ${state.error}")
                }
                else -> {

                }
            }
        }

        etRegistrationBirthday.setOnClickListener {
            displayDatePickerDialog()
        }

        btnRegistrationConfirm.setOnClickListener {
            viewModel.submitAccountInfo(
                etRegistrationFirstName.trimmedText,
                etRegistrationLastName.trimmedText,
                etRegistrationDescription.trimmedText,
                selectedDate.time.time,
            )
        }
    }

    private fun displayDatePickerDialog() {
        val currentDateTime = Calendar.getInstance()

        val startYear = currentDateTime.get(Calendar.YEAR) - 18
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate.set(year, month, day)
                etRegistrationBirthday.setText(
                    DateUtils.formatDateTime(
                        requireContext(),
                        selectedDate.timeInMillis,
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                    )
                )
            },
            startYear,
            startMonth,
            startDay
        ).show()
    }

}