// domain/repo/TaskRepository.kt
package com.example.taskhelper.data

import com.example.taskhelper.data.local.TaskDao
import com.example.taskhelper.data.local.TaskEntity
import com.example.taskhelper.domain.model.Task
import com.example.taskhelper.domain.repo.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio de tareas.
 *
 * ¿Para qué sirve?
 * - Orquesta el acceso a datos locales (Room) y mapea entre *Entity* (persistencia) y *Domain* (app).
 *
 * ¿Dónde se usa?
 * - Inyectada en ViewModels o UseCases a través del **contrato** TaskRepository.
 * - El binding interfaz→implementación está en di/RepoModule con @Binds.
 */
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {

    /**
     * Devuelve un flujo reactivo con la lista de tareas.
     * - dao.observeAll() → Flow<List<TaskEntity>>
     * - map { ... }      → transformamos cada TaskEntity a Task (modelo de dominio).
     *
     * La UI (Compose) se suscribe con collectAsStateWithLifecycle y se recompondrá
     * automáticamente cuando Room emita cambios.
     */
    override fun observe(): Flow<List<Task>> =
        dao.observeAll().map {
            list -> list.map { Task(it.id, it.title, it.done) }
        }

    /**
     * Crea o actualiza una tarea.
     * - Construimos una TaskEntity mínima con el título y done=false por defecto.
     * - El DAO hace upsert (insert con REPLACE en tu DAO).
     *
     * Nota: si en el futuro añades más campos, mapea aquí el dominio a la entity completa.
     */
    override suspend fun add(title: String) =
        dao.upsert(TaskEntity(title = title))

    /**
     * Marca una tarea como hecha/no hecha.
     * - Delegamos en el DAO que ejecuta un UPDATE por id.
     */
    override suspend fun setDone(id: Long, done: Boolean) =
        dao.setDone(id, done)

    /**
     * Elimina una tarea.
     * - Room @Delete necesita una Entity: creamos una con la PK (id) y valores "dummy".
     *   Room usará la PK para borrar la fila.
     *
     * Sugerencia: si prefieres evitar crear una Entity "vacía", añade en el DAO:
     *   @Query("DELETE FROM tasks WHERE id = :id") suspend fun deleteById(id: Long)
     * y aquí llama a dao.deleteById(id).
     */
    override suspend fun remove(id: Long) =
        dao.delete(TaskEntity(id = id, title = "", done = false))
}
