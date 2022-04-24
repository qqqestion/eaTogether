package ru.blackbull.eatogether.core

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


@Deprecated("")
fun Fragment.snackbar(msg: String) = Snackbar.make(
    requireView() ,
    msg ,
    Snackbar.LENGTH_LONG
).show()
