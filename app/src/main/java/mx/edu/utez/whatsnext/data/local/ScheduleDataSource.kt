package mx.edu.utez.whatsnext.data.local

import mx.edu.utez.whatsnext.domain.model.ClassSession

interface ScheduleDataSource {
    suspend fun getAllClasses(): List<ClassSession>
}