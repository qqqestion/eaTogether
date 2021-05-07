package ru.blackbull.eatogether.other

import java.text.SimpleDateFormat
import java.util.*

object PhotoUtility {

    fun getFormattedTime(date: Date): String {
        val formatter = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return formatter.format(date)
    }
}