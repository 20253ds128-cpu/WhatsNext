package mx.edu.utez.whatsnext.data.repository

import mx.edu.utez.whatsnext.data.local.dao.ClassSessionDao
import mx.edu.utez.whatsnext.data.local.entity.toDomain
import mx.edu.utez.whatsnext.data.local.entity.toEntity
import mx.edu.utez.whatsnext.domain.model.ClassSession
import mx.edu.utez.whatsnext.domain.model.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek

class ScheduleRepositoryImpl(
    private val dao: ClassSessionDao
) : ScheduleRepository {

    override fun getAllClasses(): Flow<List<ClassSession>> {
        return dao.getAllClasses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getClassesByDay(day: DayOfWeek): List<ClassSession> {
        return dao.getClassesByDay(day).map { it.toDomain() }
    }

    override suspend fun saveClass(classSession: ClassSession) {
        dao.insertClass(classSession.toEntity())
    }

    override suspend fun deleteClass(classSession: ClassSession) {
        dao.deleteClass(classSession.toEntity())
    }
}