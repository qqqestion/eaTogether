package ru.blackbull.eatogether.extensions

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


fun AppCompatActivity.shortToast(msg: String) {
    Toast.makeText(this , msg , Toast.LENGTH_SHORT).show()
}