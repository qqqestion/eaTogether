package ru.blackbull.eatogether.ui.main

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Fragment.snackbar(msg: String) = Snackbar.make(
    requireView() ,
    msg ,
    Snackbar.LENGTH_LONG
).show()
