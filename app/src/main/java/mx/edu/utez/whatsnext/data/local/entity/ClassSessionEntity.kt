package mx.edu.utez.whatsnext.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.edu.utez.whatsnext.domain.model.ClassSession
import java.time.DayOfWeek
import java.time.LocalTime

@Entity(tableName = "class_sessions")
data class ClassSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectName: String,
    val classroom: String,
    val building: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime
)

fun ClassSessionEntity.toDomain(): ClassSession = ClassSession(
    id = id,
    subjectName = subjectName,
    classroom = classroom,
    building = building,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime
)

fun ClassSession.toEntity(): ClassSessionEntity = ClassSessionEntity(
    id = id,
    subjectName = subjectName,
    classroom = classroom,
    building = building,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime
)