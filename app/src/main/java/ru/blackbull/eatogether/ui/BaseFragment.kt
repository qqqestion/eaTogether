package ru.blackbull.eatogether.ui

import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_main.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.auth.AuthActivity
import ru.blackbull.eatogether.ui.main.MainActivity
import timber.log.Timber

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    fun showLoadingBar() = loadingBarVisibility(true)

    fun hideLoadingBar() = loadingBarVisibility(false)

    private fun loadingBarVisibility(isVisible: Boolean) = with(activity) {
        if (this is MainActivity) {
            Timber.d("Main activity loading bar is ${if (isVisible) "visible" else "invisible"}")
            this.mainLoadingProgressBar.isVisible = isVisible
        } else if (this is AuthActivity) {
            Timber.d("Auth activity loading bar is ${if (isVisible) "visible" else "invisible"}")
            this.authLoadingProgressBar.isVisible = isVisible
        }
    }

    fun snackbar(msg: String) = Snackbar.make(
        requireView() ,
        msg ,
        Snackbar.LENGTH_LONG
    ).show()

    fun showErrorDialog(errorMessage: String) =
        MaterialAlertDialogBuilder(requireContext() , R.style.TestDialogTheme)
            .setTitle("Ошибка")
            .setMessage(errorMessage)
            .setPositiveButton("OK" , null)
            .create()
            .show()
}