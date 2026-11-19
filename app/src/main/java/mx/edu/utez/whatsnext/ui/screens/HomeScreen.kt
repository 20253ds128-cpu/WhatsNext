package mx.edu.utez.whatsnext.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.whatsnext.domain.model.ClassSession
import mx.edu.utez.whatsnext.ui.ScheduleViewModel
import mx.edu.utez.whatsnext.util.TimeFormatter
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = viewModel()
) {
    val classes by viewModel.classes.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mi Horario", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar clase")
            }
        }
    ) { innerPadding ->
        if (classes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes clases.\nToca + para agregar tu horario.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val groupedClasses = classes.groupBy { it.dayOfWeek }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedClasses.forEach { (day, dayClasses) ->
                    item(key = day.name) {
                        val dayName = day.getDisplayName(TextStyle.FULL, Locale("es", "MX"))
                            .replaceFirstChar { it.uppercase() }
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(dayClasses, key = { it.id }) { session ->
                        ClassSessionCard(
                            session = session,
                            onDelete = { viewModel.deleteClass(session) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddClassDialog(
                onDismiss = { showAddDialog = false },
                onSave = { subject, classroom, building, day, start, end ->
                    viewModel.addClass(subject, classroom, building, day, start, end)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun ClassSessionCard(
    session: ClassSession,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.subjectName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${session.classroom} • ${session.building}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${TimeFormatter.formatTime(session.startTime)} - ${TimeFormatter.formatTime(session.endTime)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar clase",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddClassDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, DayOfWeek, LocalTime, LocalTime) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var classroom by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }

    var startTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(9, 30)) }

    var pickingStartTime by remember { mutableStateOf(false) }
    var pickingEndTime by remember { mutableStateOf(false) }

    val days = listOf(
        DayOfWeek.MONDAY to "Lun",
        DayOfWeek.TUESDAY to "Mar",
        DayOfWeek.WEDNESDAY to "Mié",
        DayOfWeek.THURSDAY to "Jue",
        DayOfWeek.FRIDAY to "Vie",
        DayOfWeek.SATURDAY to "Sáb"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Clase") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Materia (ej. Cálculo)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = classroom,
                        onValueChange = { classroom = it },
                        label = { Text("Aula (ej. Aula 10)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = building,
                        onValueChange = { building = it },
                        label = { Text("Edificio (ej. Doc. 1)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Selector rápido de día
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    days.forEach { (day, label) ->
                        FilterChip(
                            selected = selectedDay == day,
                            onClick = { selectedDay = day },
                            label = { Text(label) }
                        )
                    }
                }

                // Botones para elegir hora de inicio y fin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { pickingStartTime = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Inicio: ${TimeFormatter.formatTime(startTime)}")
                    }
                    OutlinedButton(
                        onClick = { pickingEndTime = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fin: ${TimeFormatter.formatTime(endTime)}")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(subject, classroom, building, selectedDay, startTime, endTime) },
                enabled = subject.isNotBlank() && classroom.isNotBlank() && endTime.isAfter(startTime)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    if (pickingStartTime) {
        TimePickerModal(
            initialTime = startTime,
            onDismiss = { pickingStartTime = false },
            onConfirm = {
                startTime = it
                if (!endTime.isAfter(it)) {
                    endTime = it.plusHours(1)
                }
                pickingStartTime = false
            }
        )
    }

    if (pickingEndTime) {
        TimePickerModal(
            initialTime = endTime,
            onDismiss = { pickingEndTime = false },
            onConfirm = {
                endTime = it
                pickingEndTime = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerModal(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            TimePicker(state = state)
        }
    )
}