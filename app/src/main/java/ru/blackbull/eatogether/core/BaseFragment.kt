package ru.blackbull.eatogether.core

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.MainActivity
import timber.log.Timber
import kotlin.reflect.KClass

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    fun showLoadingBar() = loadingBarVisibility(true)

    fun hideLoadingBar() = loadingBarVisibility(false)

    private fun loadingBarVisibility(isVisible: Boolean) = with(activity) {
        if (this is MainActivity) {
            Timber.d("Main activity loading bar is ${if (isVisible) "visible" else "invisible"}")
            this.mainLoadingProgressBar.isVisible = isVisible
        }
    }

    fun snackbar(msg: String) = Snackbar.make(
        requireView(),
        msg,
        Snackbar.LENGTH_LONG
    ).show()

    fun snackbar(@StringRes msgId: Int) = snackbar(getString(msgId))

    fun hideKeyboard() {
        requireActivity().currentFocus?.let {
            val imm =
                requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showErrorDialog(errorMessage: String) =
        MaterialAlertDialogBuilder(requireContext(), R.style.TestDialogTheme)
            .setTitle("Ошибка")
            .setMessage(errorMessage)
            .setPositiveButton("OK", null)
            .create()
            .show()
}

abstract class BaseFragmentV2<VM : BaseViewModel>(
    @LayoutRes layoutId: Int,
    viewModelClazz: KClass<VM>,
) : Fragment(layoutId) {

    open val factoryProducer: (() -> ViewModelProvider.Factory)?
        get() = null

    protected val viewModel: VM by createViewModelLazy(
        viewModelClazz,
        { viewModelStore },
        factoryProducer
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.navigationCommands
                .collect { navCommand ->
                    when (navCommand) {
                        is NavigationCommand.To -> findNavController().navigate(
                            navCommand.direction
                        )
                    }
                }
        }
    }

    fun hideKeyboard() {
        requireActivity().currentFocus?.let {
            val imm =
                requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun snackbar(msg: String) = Snackbar.make(
        requireView(),
        msg,
        Snackbar.LENGTH_LONG
    ).show()

    fun snackbar(@StringRes msgId: Int) = snackbar(getString(msgId))

    fun showErrorDialog(errorMessage: String) =
        MaterialAlertDialogBuilder(requireContext(), R.style.TestDialogTheme)
            .setTitle("Ошибка")
            .setMessage(errorMessage)
            .setPositiveButton("OK", null)
            .create()
            .show()

    fun showErrorDialog(@StringRes errorMessageId: Int) = showErrorDialog(getString(errorMessageId))

    fun showLoadingBar() = loadingBarVisibility(true)

    fun hideLoadingBar() = loadingBarVisibility(false)

    private fun loadingBarVisibility(isVisible: Boolean) = with(activity) {
        if (this is MainActivity) {
            Timber.d("Main activity loading bar is ${if (isVisible) "visible" else "invisible"}")
            this.mainLoadingProgressBar.isVisible = isVisible
        }
    }
}