package mx.edu.utez.whatsnext.domain.usecase

import mx.edu.utez.whatsnext.domain.model.ClassSession
import mx.edu.utez.whatsnext.domain.model.ScheduleStatus
import java.time.LocalTime
import kotlin.collections.sortedBy

class GetCurrentClassStatusUseCase {

    operator fun invoke(
        todayClasses: List<ClassSession>,
        currentTime: LocalTime = LocalTime.now()
    ): ScheduleStatus {
        if (todayClasses.isEmpty()) {
            return ScheduleStatus.NoClassesToday
        }

        // Aseguramos orden cronológico por hora de inicio
        val sortedClasses = todayClasses.sortedBy { it.startTime }

        // 1. ¿Hay alguna clase en curso ahora mismo? (startTime <= currentTime < endTime)
        val currentIndex = sortedClasses.indexOfFirst { session ->
            !currentTime.isBefore(session.startTime) && currentTime.isBefore(session.endTime)
        }

        if (currentIndex != -1) {
            val current = sortedClasses[currentIndex]
            val next = sortedClasses.getOrNull(currentIndex + 1)
            return ScheduleStatus.InClass(
                currentClass = current,
                nextClass = next
            )
        }

        // 2. Si no está en clase, buscar la próxima clase cuya hora de inicio sea posterior a ahora
        val nextIndex = sortedClasses.indexOfFirst { session ->
            currentTime.isBefore(session.startTime)
        }

        if (nextIndex != -1) {
            return ScheduleStatus.Upcoming(
                nextClass = sortedClasses[nextIndex],
                isBeforeFirstClass = nextIndex == 0
            )
        }

        // 3. Si todas las clases del día ya terminaron
        return ScheduleStatus.FinishedForToday
    }
}