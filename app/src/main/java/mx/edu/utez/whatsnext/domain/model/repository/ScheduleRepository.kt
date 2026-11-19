package mx.edu.utez.whatsnext.domain.model.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.utez.whatsnext.domain.model.ClassSession
import java.time.DayOfWeek

interface ScheduleRepository {
    fun getAllClasses(): Flow<List<ClassSession>>
    suspend fun getClassesByDay(day: DayOfWeek): List<ClassSession>
    suspend fun saveClass(classSession: ClassSession)
    suspend fun deleteClass(classSession: ClassSession)
}