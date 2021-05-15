package ru.blackbull.eatogether.ui.auth.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration_step_two.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.AuthViewModel
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class RegistrationStepTwoFragment : BaseFragment(R.layout.fragment_registration_step_two) {

    private val viewModel: AuthViewModel by viewModels()

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
            viewModel.signUp(
                etRegistrationFirstName.text.toString() ,
                etRegistrationLastName.text.toString() ,
                selectedDate.time ,
                etRegistrationDescription.text.toString()
            )
        }
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
        viewModel.signUpResult.observe(viewLifecycleOwner , EventObserver(
            onError = {
                hideLoadingBar()
                showErrorDialog(it)
            } ,
            onLoading = {
                showLoadingBar()
            }
        ) {
            hideLoadingBar()
            Intent(requireContext() , MainActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        })
    }
}