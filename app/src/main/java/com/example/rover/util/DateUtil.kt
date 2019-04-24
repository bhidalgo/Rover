package com.example.rover.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    const val ROVER_DATE_FORMAT = "MMMMM dd yyyy"

    @JvmStatic
    fun toString(rawDate: String): String {
        val format = SimpleDateFormat(ROVER_DATE_FORMAT, Locale.US)

        val date = format.parse(rawDate)

        return date.toString()
    }
}