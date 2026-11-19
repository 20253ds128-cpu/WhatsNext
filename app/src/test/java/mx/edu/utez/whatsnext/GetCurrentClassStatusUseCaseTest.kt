package mx.edu.utez.whatsnext

import mx.edu.utez.whatsnext.domain.model.ClassSession
import mx.edu.utez.whatsnext.domain.model.ScheduleStatus
import mx.edu.utez.whatsnext.domain.usecase.GetCurrentClassStatusUseCase
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

class GetCurrentClassStatusUseCaseTest {

    private val useCase = GetCurrentClassStatusUseCase()

    private val sampleSchedule = listOf(
        ClassSession(
            id = 1,
            subjectName = "Cálculo",
            classroom = "Aula 10",
            building = "Docencia 1",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(10, 0)
        ),
        ClassSession(
            id = 2,
            subjectName = "Programación Móvil",
            classroom = "Lab 3",
            building = "Docencia 2",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(13, 0)
        )
    )

    @Test
    fun `when no classes today returns NoClassesToday`() {
        val result = useCase(emptyList(), LocalTime.of(9, 0))
        assertEquals(ScheduleStatus.NoClassesToday, result)
        assertNull(result.nextTransitionTime)
    }

    @Test
    fun `before first class returns Upcoming with isBeforeFirstClass true`() {
        val result = useCase(sampleSchedule, LocalTime.of(7, 30))
        assertTrue(result is ScheduleStatus.Upcoming)
        val upcoming = result as ScheduleStatus.Upcoming
        assertEquals("Cálculo", upcoming.nextClass.subjectName)
        assertTrue(upcoming.isBeforeFirstClass)
        assertEquals(LocalTime.of(8, 0), upcoming.nextTransitionTime)
    }

    @Test
    fun `during first class returns InClass with nextClass populated`() {
        val result = useCase(sampleSchedule, LocalTime.of(8, 0))
        assertTrue(result is ScheduleStatus.InClass)
        val inClass = result as ScheduleStatus.InClass
        assertEquals("Cálculo", inClass.currentClass.subjectName)
        assertEquals("Programación Móvil", inClass.nextClass?.subjectName)
        assertEquals(LocalTime.of(10, 0), inClass.nextTransitionTime)
    }

    @Test
    fun `between classes returns Upcoming with isBeforeFirstClass false`() {
        val result = useCase(sampleSchedule, LocalTime.of(10, 15))
        assertTrue(result is ScheduleStatus.Upcoming)
        val upcoming = result as ScheduleStatus.Upcoming
        assertEquals("Programación Móvil", upcoming.nextClass.subjectName)
        assertEquals(false, upcoming.isBeforeFirstClass)
        assertEquals(LocalTime.of(11, 0), upcoming.nextTransitionTime)
    }

    @Test
    fun `after last class returns FinishedForToday`() {
        val result = useCase(sampleSchedule, LocalTime.of(13, 0))
        assertEquals(ScheduleStatus.FinishedForToday, result)
        assertNull(result.nextTransitionTime)
    }
}