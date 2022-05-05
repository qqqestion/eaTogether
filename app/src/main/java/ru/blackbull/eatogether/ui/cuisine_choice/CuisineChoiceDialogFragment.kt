package ru.blackbull.eatogether.ui.cuisine_choice

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_cuisine_choice.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.R
import timber.log.Timber


@AndroidEntryPoint
class CuisineChoiceDialogFragment : DialogFragment() {

    private var dialogView: View? = null

    private val viewModel: CuisineChoiceViewModel by viewModels()

    /**
     * Создаем view для диалога и ставим кнопку "подтвердить",
     * чтобы во viewmodel загонять кухни (среди них отмеченные и нет)
     *
     * @param savedInstanceState
     * @return
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_cuisine_choice,
            null
        )
        Timber.d("onCreateDialog")

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выбор кухни")
            .setView(dialogView)
            .setPositiveButton("Подтвердить") { _, _ -> }
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cuisineAdapter = CuisineAdapter { cuisine ->
            viewModel.toggleCuisine(cuisine)
        }
        rvCuisine.adapter = cuisineAdapter
        rvCuisine.layoutManager = GridLayoutManager(requireContext(), 2)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cuisines.collect { state ->
                    cuisineAdapter.cuisines = state.cuisines
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveSelected()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogView = null
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"

        fun getInstance() = CuisineChoiceDialogFragment()

        fun showOnlyOnce(fragmentManager: FragmentManager) {
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                getInstance().show(
                    fragmentManager,
                    TAG
                )
            }
        }
    }
}