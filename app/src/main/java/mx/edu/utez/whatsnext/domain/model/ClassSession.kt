package mx.edu.utez.whatsnext.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

data class ClassSession(
    val id: Int = 0,
    val subjectName: String,
    val classroom: String,
    val building: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime
)