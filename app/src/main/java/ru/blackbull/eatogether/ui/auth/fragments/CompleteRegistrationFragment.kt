package ru.blackbull.eatogether.ui.auth.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.blackbull.domain.usecases.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.databinding.FragmentCompleteRegistrationBinding
import ru.blackbull.eatogether.ui.auth.CompleteRegistrationState
import ru.blackbull.eatogether.ui.auth.CompleteRegistrationViewModel
import ru.blackbull.eatogether.ui.clearError
import ru.blackbull.eatogether.ui.trimmedText
import java.util.*

@AndroidEntryPoint
class CompleteRegistrationFragment : BaseFragmentV2<CompleteRegistrationViewModel>(
    R.layout.fragment_complete_registration, CompleteRegistrationViewModel::class
) {

    private val binding: FragmentCompleteRegistrationBinding by viewBinding(
        FragmentCompleteRegistrationBinding::bind
    )

    private val selectedDate = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            val isLoading = (state is CompleteRegistrationState.Loading)

            binding.btnRegistrationConfirm.isEnabled = isLoading.not()
            binding.progressBar.isVisible = isLoading
            when (state) {
                is CompleteRegistrationState.Error -> handleError(state.error)
                else -> {
                    with(binding) {
                        tilRegistrationFirstName.clearError()
                        tilRegistrationLastName.clearError()
                        tilRegistrationBirthday.clearError()
                        tilRegistrationDescription.clearError()
                    }
                }
            }
        }

        binding.etRegistrationBirthday.setOnClickListener {
            displayDatePickerDialog()
        }

        binding.btnRegistrationConfirm.setOnClickListener {
            viewModel.completeRegistration(
                binding.etRegistrationFirstName.trimmedText,
                binding.etRegistrationLastName.trimmedText,
                binding.etRegistrationDescription.trimmedText,
                selectedDate.time.time,
            )
        }
    }

    private fun handleError(error: CompleteRegistrationUseCaseError) {
        Log.d("!!!", "Error on CompleteRegistration screen: $error")
        when (error) {
            BirthdayError -> TODO()
            FirstNameFormatError -> TODO()
            LastNameFormatError -> TODO()
            NoInternetError -> TODO()
            UnexpectedNetworkCommunicationError -> TODO()
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
                binding.etRegistrationBirthday.setText(
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