package mx.edu.utez.whatsnext.data.local

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalTime

class ScheduleConverters {
    @TypeConverter
    fun fromDayOfWeek(day: DayOfWeek): Int = day.value // 1 (Lunes) a 7 (Domingo)

    @TypeConverter
    fun toDayOfWeek(value: Int): DayOfWeek = DayOfWeek.of(value)

    @TypeConverter
    fun fromLocalTime(time: LocalTime): Int = time.toSecondOfDay()

    @TypeConverter
    fun toLocalTime(seconds: Int): LocalTime = LocalTime.ofSecondOfDay(seconds.toLong())
}