package mx.edu.utez.whatsnext.ui

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import mx.edu.utez.whatsnext.data.local.AppDatabase
import mx.edu.utez.whatsnext.data.repository.ScheduleRepositoryImpl
import mx.edu.utez.whatsnext.domain.model.ClassSession
import mx.edu.utez.whatsnext.widget.ScheduleWidget
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScheduleRepositoryImpl

    val classes: StateFlow<List<ClassSession>>

    init {
        val dao = AppDatabase.getInstance(application).classSessionDao()
        repository = ScheduleRepositoryImpl(dao)

        classes = repository.getAllClasses().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    }

    fun addClass(
        subjectName: String,
        classroom: String,
        building: String,
        dayOfWeek: DayOfWeek,
        startTime: LocalTime,
        endTime: LocalTime
    ) {
        if (subjectName.isBlank() || classroom.isBlank()) return

        viewModelScope.launch {
            val newSession = ClassSession(
                subjectName = subjectName.trim(),
                classroom = classroom.trim(),
                building = building.trim().ifEmpty { "General" },
                dayOfWeek = dayOfWeek,
                startTime = startTime,
                endTime = endTime
            )
            repository.saveClass(newSession)
            ScheduleWidget().updateAll(getApplication())
        }
    }

    fun deleteClass(session: ClassSession) {
        viewModelScope.launch {
            repository.deleteClass(session)
            ScheduleWidget().updateAll(getApplication())
        }
    }
}