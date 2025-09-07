// data/local/TaskDao.kt
package com.example.taskhelper.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * TaskDao
 * ------
 * DAO (Data Access Object) de Room para la tabla "tasks".
 *
 * ¿Para qué sirve?
 * - Declara las operaciones SQL de alto nivel (consultas, inserts, updates, deletes).
 * - Room genera la implementación concreta en compilación (vía KSP).
 *
 * ¿Dónde se usa?
 * - Inyectado en el repositorio (p. ej. TaskRepositoryImpl) para acceder a datos locales.
 * - Desde el ViewModel, el repo expone Flows que la UI (Compose) colecta y muestra.
 */
@Dao
interface TaskDao {
    /**
     * Devuelve un stream (Flow) con **todas** las tareas, ordenadas por id descendente.
     * - @Query: SQL que ejecutará Room.
     * - Flow<List<TaskEntity>>: cada vez que la tabla cambie, el Flow emite una lista nueva.
     *
     * Uso típico:
     * - Repo: `dao.observeAll()` → mapea a modelo de dominio → expone `Flow<List<Task>>`.
     * - ViewModel: recoge ese Flow y lo expone como StateFlow.
     * - Compose: `collectAsStateWithLifecycle()` para renderizar en UI en tiempo real.
     */
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    /**
     * Inserta o reemplaza una tarea.
     * - suspend: se ejecuta en corrutina (Room manejará el thread adecuado).
     * - OnConflictStrategy.REPLACE: si existe misma clave primaria, reemplaza la fila.
     *   Ojo: REPLACE hace un DELETE+INSERT bajo el capó; si tienes claves foráneas puede importar.
     *
     * Tip:
     * - En Room 2.6+ existe @Upsert que combina insert/update; con 2.7.2 podrías usar:
     *   @Upsert suspend fun upsert(entity: TaskEntity)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TaskEntity)

    /**
     * Marca una tarea como hecha/no hecha.
     * - @Query con parámetros nombrados (:id, :done) que se sustituyen por los args de la función.
     * - suspend: operación de I/O; Room la ejecuta fuera del main thread.
     *
     * Uso típico:
     * - Repo: `dao.setDone(id, true/false)` desde un caso de uso (ToggleTask).
     */
    @Query("UPDATE tasks SET done = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)

    /**
     * Elimina una fila concreta.
     * - @Delete: Room genera el SQL DELETE usando la PK del entity.
     *
     * Uso típico:
     * - Repo: `dao.delete(entity)` cuando el usuario borra la tarea.
     *   (También se podría hacer con @Query("DELETE FROM tasks WHERE id = :id"))
     */
    @Delete
    suspend fun delete(entity: TaskEntity)
}
