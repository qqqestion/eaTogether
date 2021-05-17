package ru.blackbull.eatogether.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.search.SearchManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_cuisine_choice.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.CuisineAdapter
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.ui.main.map.MapViewModel
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class CuisineChoiceDialogFragment : DialogFragment() {

    private var dialogView: View? = null

    private val viewModel: MapViewModel by activityViewModels()

    @Inject
    lateinit var searchManager: SearchManager

    private val cuisineAdapter = CuisineAdapter()

    /**
     * Создаем view для диалога и ставим кнопку "подтвердить",
     * чтобы во viewmodel загонять кухни (среди них отмеченные и нет)
     *
     * @param savedInstanceState
     * @return
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_cuisine_choice ,
            null
        )
        Timber.d("onCreateDialog")

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выбор кухни")
            .setView(dialogView)
            .setPositiveButton("Подтвердить") { _ , _ ->
                viewModel.cuisine.postValue(Event(Resource.Success(cuisineAdapter.cuisines)))
            }
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        return dialogView
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        Timber.d("onViewCreated")
        subscribeToObservers()
        viewModel.getCuisineList()
    }

    private fun setupRecyclerView() {
        rvCuisine.adapter = cuisineAdapter
        rvCuisine.layoutManager = GridLayoutManager(requireContext() , 2)
    }

    private fun subscribeToObservers() {
        viewModel.cuisine.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
                Timber.d(it)
            }
        ) {
            cuisineAdapter.cuisines = it
            Timber.d("Cuisine list: ${it.map { cuisine -> cuisine.name }}")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogView = null
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}