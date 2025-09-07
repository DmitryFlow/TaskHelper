// domain/repo/TaskRepository.kt
package com.example.taskhelper.domain.repo
import com.example.taskhelper.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * TaskRepository
 * --------------
 * Contrato de dominio para gestionar tareas.
 *
 * - ¿Para qué sirve?
 *   Define qué operaciones ofrece el sistema respecto a "tareas"
 *   sin comprometerse con *cómo* se implementan (DB local, red, memoria, etc.).
 *
 * - ¿Dónde se usa?
 *   - En ViewModels y UseCases (p. ej., `ObserveTasks`, `AddTask`, `ToggleTask`).
 *   - En la UI (indirectamente): la UI observa Flows expuestos por el VM.
 *
 * - ¿Quién lo implementa?
 *   `TaskRepositoryImpl` en la capa `data/`, que orquesta DAO de Room, remotos, mapeos, etc.
 *   El enlace interfaz→implementación se declara en `di/RepoModule` con `@Binds`.
 */
interface TaskRepository {
    // Observa todas las tareas como un stream reactivo.
    // - Devuelve Flow<List<Task>>: cada cambio en la fuente (Room) emite una lista nueva.
    // - La UI lo consumirá con collectAsStateWithLifecycle para recomponerse automáticamente.
    fun observe(): Flow<List<Task>>

    // Crea una nueva tarea con el título dado.
    // - suspend: operación de E/S (DB) que no debe ejecutarse en el hilo principal.
    // - Implementación típica: mapear a TaskEntity y llamar a DAO.upsert(...)
    suspend fun add(title: String)

    // Marca/desmarca una tarea como completada.
    // - suspend: actualización en la base de datos.
    // - Implementación típica: DAO.setDone(id, done)
    suspend fun setDone(id: Long, done: Boolean)

    // Elimina una tarea por id (o por entidad).
    // - suspend: operación de escritura.
    // - Implementación típica: DAO.delete(...)
    suspend fun remove(id: Long)
}
