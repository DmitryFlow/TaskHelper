// data/local/TaskEntity.kt
package com.example.taskhelper.data.local

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
    // Clave primaria de la tabla. autoGenerate = true hace que Room asigne el id en cada INSERT.
    // Usamos Long para cubrir muchos registros y por compat con autoincrement.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // Columna "title": título de la tarea. No tiene valor por defecto → campo obligatorio al crear una entidad.
    val title: String,
    // Columna "done": indica si la tarea está completada. Por defecto false.
    // Útil para togglear desde UI sin tener que pasar siempre el valor en cada creación.
    val done: Boolean = false
)
