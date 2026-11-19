package mx.edu.utez.whatsnext.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import mx.edu.utez.whatsnext.MainActivity
import mx.edu.utez.whatsnext.data.local.AppDatabase
import mx.edu.utez.whatsnext.data.repository.ScheduleRepositoryImpl
import mx.edu.utez.whatsnext.domain.model.ScheduleStatus
import mx.edu.utez.whatsnext.domain.usecase.GetCurrentClassStatusUseCase
import mx.edu.utez.whatsnext.util.TimeFormatter
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

class ScheduleWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)
        val repository = ScheduleRepositoryImpl(db.classSessionDao())
        val useCase = GetCurrentClassStatusUseCase()

        val today = LocalDate.now().dayOfWeek
        val todayClasses = repository.getClassesByDay(today)
        val status = useCase(todayClasses)

        val dayLabel = today.getDisplayName(JavaTextStyle.FULL, Locale("es", "MX"))
            .replaceFirstChar { it.uppercase() }

        provideContent {
            GlanceTheme {
                ScheduleWidgetContent(
                    dayLabel = dayLabel,
                    status = status
                )
            }
        }
    }
}

@Composable
private fun ScheduleWidgetContent(
    dayLabel: String,
    status: ScheduleStatus
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(20.dp)
            .background(GlanceTheme.colors.surface)
            .clickable(actionStartActivity<MainActivity>())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (status) {
            is ScheduleStatus.InClass -> {
                val current = status.currentClass
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$dayLabel • EN CURSO",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "${current.subjectName} • ${current.classroom} (${current.building})",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                status.nextClass?.let { next ->
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = "Sigue: ${next.subjectName} (${next.classroom} - ${TimeFormatter.formatTime(next.startTime)})",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            is ScheduleStatus.Upcoming -> {
                val next = status.nextClass
                val tag = if (status.isBeforeFirstClass) "PRIMERA CLASE" else "PRÓXIMA CLASE"
                Text(
                    text = "$dayLabel • $tag (${TimeFormatter.formatTime(next.startTime)})",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "${next.subjectName} • ${next.classroom} (${next.building})",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

            ScheduleStatus.FinishedForToday -> {
                Text(
                    text = "$dayLabel • JORNADA TERMINADA",
                    style = TextStyle(
                        color = GlanceTheme.colors.secondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "Sin más clases por hoy",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp
                    )
                )
            }

            ScheduleStatus.NoClassesToday -> {
                Text(
                    text = dayLabel,
                    style = TextStyle(
                        color = GlanceTheme.colors.secondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "Día libre • Sin clases programadas",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}