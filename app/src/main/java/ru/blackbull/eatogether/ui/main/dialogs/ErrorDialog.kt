package ru.blackbull.eatogether.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.blackbull.eatogether.R

class ErrorDialog(
    private val errorMessage: String
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext() , R.style.TestDialogTheme)
            .setTitle("Ошибка")
            .setMessage(errorMessage)
            .setPositiveButton("OK" , null)
            .create()
    }

//    override fun onCreateView(
//        inflater: LayoutInflater ,
//        container: ViewGroup? ,
//        savedInstanceState: Bundle?
//    ): View {
//        return LayoutInflater.from(requireContext()).inflate(
//            R.layout.fragment_error ,
//            null
//        )
//    }
//
//    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
//        super.onViewCreated(view , savedInstanceState)
//        tvErrorMessage.text = errorMessage
//    }
}