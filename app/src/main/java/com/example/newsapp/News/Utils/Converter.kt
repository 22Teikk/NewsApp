package com.example.newsapp.News.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class Converter {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun FormatFullDate(inputDate: String): String {
            val inputFormatter = DateTimeFormatter.ISO_INSTANT
            val outputFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH)

            return try {
                val instant = Instant.parse(inputDate)
                val zonedDateTime = instant.atZone(ZoneId.systemDefault())
                val formattedDate = zonedDateTime.format(outputFormatter)

                formattedDate
            } catch (e: Exception) {
                "Invalid Date"
            }
        }
    }
}