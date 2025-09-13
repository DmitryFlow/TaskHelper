// data/task/local/TaskEntity.kt
package com.example.taskhelper.data.task.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TaskEntity
 * ----------
 * Modelo de **persistencia** para Room: representa una fila en la tabla "tasks".
 *
 * ¿Para qué sirve?
 * - Es el "schema" que Room usa para crear la tabla y mapear filas ↔ objetos Kotlin.
 *
 * ¿Dónde se usa?
 * - En la base de datos `AppDatabase` (annot @Database(..., entities = [TaskEntity::class])).
 * - En el DAO `TaskDao` (consultas @Query/@Insert/@Delete devuelven/reciben TaskEntity).
 * - En el repositorio (`TaskRepositoryImpl`) se mapea <TaskEntity> ↔ <Task> (modelo de dominio).
 *
 * Nota: mantenemos **TaskEntity** (data layer) separado de **Task** (domain layer) para no acoplar
 * la capa de dominio al detalle de Room (facilita test, migraciones y cambios de storage).
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,   // PK autogenerada por Room
    val title: String,                                   // Título persistido
    val completed: Boolean = false,                      // Propiedad alineada con el dominio
    val createdAtEpochSeconds: Long? = null              // Instant? -> converter
)
