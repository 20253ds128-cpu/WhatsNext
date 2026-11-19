package mx.edu.utez.whatsnext.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeFormatter {
    private val defaultFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    fun formatTime(time: LocalTime): String = time.format(defaultFormatter)
}