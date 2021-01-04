package ru.blackbull.eatogether.extensions

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


fun AppCompatActivity.shortToast(msg: String) {
    Toast.makeText(this , msg , Toast.LENGTH_SHORT).show()
}

fun Fragment.shortToast(msg: String) {
    Toast.makeText(context , msg , Toast.LENGTH_SHORT).show()
}
