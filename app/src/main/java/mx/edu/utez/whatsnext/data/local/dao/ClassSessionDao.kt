package mx.edu.utez.whatsnext.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mx.edu.utez.whatsnext.data.local.entity.ClassSessionEntity
import java.time.DayOfWeek

@Dao
interface ClassSessionDao {

    @Query("SELECT * FROM class_sessions ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllClasses(): Flow<List<ClassSessionEntity>>

    @Query("SELECT * FROM class_sessions WHERE dayOfWeek = :day ORDER BY startTime ASC")
    suspend fun getClassesByDay(day: DayOfWeek): List<ClassSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classSession: ClassSessionEntity)

    @Delete
    suspend fun deleteClass(classSession: ClassSessionEntity)
}