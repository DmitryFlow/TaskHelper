package com.example.taskhelper.data.task.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskhelper.data.task.local.TaskEntity
import com.example.taskhelper.data.task.remote.TaskDto
import com.example.taskhelper.domain.task.model.Task
import java.time.Instant

/** Convierte una Entity de Room (persistencia) a un modelo de dominio. */
@RequiresApi(Build.VERSION_CODES.O)
fun TaskEntity.toDomain(): Task = // Función de extensión sobre TaskEntity para mejorar legibilidad en el repo.
    Task(                              // Construimos el objeto de dominio que verán los casos de uso y la UI.
        id = id,                       // La PK de la fila se traslada tal cual al dominio.
        title = title,                 // El título persistido pasa al modelo de dominio.
        completed = completed,         // Estado lógico de completado (entity → domain).
        createdAt =                    // Fecha de creación en dominio como Instant? (nullable).
            createdAtEpochSeconds      // En DB la guardamos como epoch seconds (Long?).
                ?.let(Instant::ofEpochSecond) // Si no es null, la convertimos a Instant; si es null, queda null.
    )

/** Convierte un DTO de red (JSON) a una Entity de Room (para cachear en DB). */
@RequiresApi(Build.VERSION_CODES.O)
fun TaskDto.toEntity(): TaskEntity = // Extensión sobre TaskDto; usada tras llamar al API antes de persistir.
    TaskEntity(
        id = id,                       // Si el backend define id estable, lo reusamos como PK en DB.
        title = title,                 // Copiamos el campo del JSON al esquema local.
        completed = completed,         // Estado remoto → persistencia local (mismo significado).
        createdAtEpochSeconds =        // Persistimos la fecha como epoch seconds para evitar TypeConverters.
            createdAt                 // String? con fecha en ISO-8601 o null (p. ej., "2025-09-12T18:30:00Z").
                ?.let {               // Si no es null, intentamos parsearla de forma segura:
                    runCatching {     // runCatching evita que un parseo inválido lance y rompa la inserción.
                        Instant.parse(it).epochSecond // Parse ISO-8601 → Instant → epoch seconds (Long).
                    }.getOrNull()     // En caso de error de parseo devolvemos null (no guardamos fecha).
                }
    )

/** Convierte un DTO de red (JSON) directamente a dominio (si no quieres pasar por DB). */
@RequiresApi(Build.VERSION_CODES.O)
fun TaskDto.toDomain(): Task = // Útil para flujos "solo red" o para componer respuestas híbridas.
    Task(
        id = id,                       // El identificador remoto pasa tal cual al dominio.
        title = title,                 // El título remoto pasa al dominio.
        completed = completed,         // Estado remoto → dominio.
        createdAt =                    // Fecha como Instant? en el dominio.
            createdAt                 // Tomamos el String? ISO-8601 del backend…
                ?.let {               // …si existe, lo intentamos parsear con seguridad:
                    runCatching {
                        Instant.parse(it) // Si el formato no es ISO-8601 válido, capturamos la excepción…
                    }.getOrNull()         // …y devolvemos null para no romper el flujo.
                }
    )
