package com.tecknobit.envui.ui.utils

import java.text.DateFormat
import java.util.*

/**
 * Method used to format this timestamp with the default date and time formatter
 *
 * @return the formatted timestamp as [String]
 */
fun Long.toDateString(): String {
    val formatter = DateFormat.getInstance()
    val date = Date(this)

    return formatter.format(date)
}
