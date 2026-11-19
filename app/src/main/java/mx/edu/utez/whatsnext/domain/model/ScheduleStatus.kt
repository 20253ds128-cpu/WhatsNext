package mx.edu.utez.whatsnext.domain.model

import java.time.LocalTime

sealed interface ScheduleStatus {
    /** El usuario no tiene ninguna clase registrada para este día */
    data object NoClassesToday : ScheduleStatus

    /** Aún no empieza la primera clase del día (o está en hora libre entre clases) */
    data class Upcoming(
        val nextClass: ClassSession,
        val isBeforeFirstClass: Boolean
    ) : ScheduleStatus

    /** El usuario está actualmente en horario de clase */
    data class InClass(
        val currentClass: ClassSession,
        val nextClass: ClassSession? // null si es la última clase del día
    ) : ScheduleStatus

    /** Ya terminó la última clase del día: momento de silenciar notificaciones */
    data object FinishedForToday : ScheduleStatus

    /**
     * Indica a qué hora exacta ocurre el siguiente cambio de estado
     * para programar el AlarmManager sin gastar batería.
     */
    val nextTransitionTime: LocalTime?
        get() = when (this) {
            is NoClassesToday -> null
            is Upcoming -> nextClass.startTime
            is InClass -> currentClass.endTime
            is FinishedForToday -> null
        }
}