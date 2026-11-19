package mx.edu.utez.whatsnext.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.room.TypeConverters
import mx.edu.utez.whatsnext.data.local.dao.ClassSessionDao
import mx.edu.utez.whatsnext.data.local.entity.ClassSessionEntity

@Database(
    entities = [ClassSessionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ScheduleConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun classSessionDao(): ClassSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uni_schedule_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}