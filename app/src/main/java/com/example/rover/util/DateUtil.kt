package com.example.rover.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    const val RAW_DATE_FORMAT = "yyyy-MM-dd"
    const val ROVER_DATE_FORMAT = "MMM dd yyyy"

    @JvmStatic
    fun toString(rawDate: String): String {
        val rawFormat = SimpleDateFormat(RAW_DATE_FORMAT, Locale.US)
        val desiredFormat = SimpleDateFormat(ROVER_DATE_FORMAT, Locale.US)

        val date = rawFormat.parse(rawDate)

        return desiredFormat.format(date)
    }
}