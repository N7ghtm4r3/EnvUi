package com.tecknobit.envui.ui.utils

import java.text.DateFormat
import java.util.*

fun Long.toDateString(): String {
    val formatter = DateFormat.getInstance()
    val date = Date(this)

    return formatter.format(date)
}
