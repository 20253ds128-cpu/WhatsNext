# 📅 UniSchedule

Aplicación nativa para Android diseñada para mostrar tu clase actual, el aula y la siguiente materia directamente en la pantalla de inicio (vía **Jetpack Glance Widget**) y en la pantalla de bloqueo (vía notificaciones silenciosas contextuales).

## ✨ Características principales
- **Contexto automático:** Muestra qué clase tienes en curso y cuál sigue según la hora y el día actual.
- **Modo silencioso fuera de horario:** Oculta las notificaciones automáticamente cuando terminas tu jornada académica.
- **100% Offline:** Almacenamiento local ultrarrápido sin depender de conexión a internet en las aulas.

## 🛠 Stack Tecnológico & Arquitectura
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Widgets:** Jetpack Glance (`AppWidget`)
- **Base de Datos:** Room (SQLite)
- **Tareas Programadas:** `AlarmManager` & `WorkManager`
- **Arquitectura:** Clean Architecture + MVVM

## 📂 Estructura del Proyecto
```text
com.tuusuario.unischedule
├── data        # Base de datos local (Room), DAOs y repositorios
├── domain      # Modelos de negocio y casos de uso (lógica de horarios)
├── ui          # Pantallas en Compose, temas y ViewModels
├── widget      # Implementación de Jetpack Glance y notificaciones
└── util        # Helpers de tiempo y programación de alarmas