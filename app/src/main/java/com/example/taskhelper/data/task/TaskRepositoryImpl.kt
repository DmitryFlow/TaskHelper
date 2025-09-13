package com.example.taskhelper.data.task

import com.example.taskhelper.core.common.CoreError
import com.example.taskhelper.core.common.Result
import com.example.taskhelper.core.coroutines.DispatcherProvider
import com.example.taskhelper.data.task.local.TaskDao
import com.example.taskhelper.data.task.local.TaskEntity
import com.example.taskhelper.domain.task.model.Task
import com.example.taskhelper.domain.task.repo.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * Implementación del repositorio de tareas en la capa DATA.
 * - Implementa el contrato de dominio TaskRepository.
 * - Traduce excepciones técnicas a errores tipados (CoreError) y expone Result<T>.
 * - Mapea entre TaskEntity (Room) y Task (dominio).
 */
class TaskRepositoryImpl @Inject constructor(          // Se marca @Inject para que Hilt pueda construirla.
    private val dao: TaskDao,                          // DAO de Room para acceder a la tabla de tareas.
    private val dispatchers: DispatcherProvider        // Proveedor de Dispatchers para IO/Main/Default (testable).
) : TaskRepository {                                   // Implementa el contrato de dominio (interfaz en 'domain').

    /**
     * Devuelve un Flow con Result<List<Task>>:
     * - Éxito: Success(lista de Task de dominio).
     * - Fallo : Failure(CoreError) si Room emite un error.
     * La UI (vía ViewModel) lo consumirá con collectAsStateWithLifecycle.
     */
    override fun getTasks(): Flow<Result<List<Task>>> =
        dao.observeAll()                               // Flow<List<TaskEntity>> emitido por Room al cambiar la tabla.
            .map { entities ->                         // Transformamos cada emisión de Room...
                Result.Success(entities.map { it.toDomain() }) as Result<List<Task>> // ...mapeando Entity -> Domain. Envolvemos en un Success tipado.
                //val domain = entities.map { it.toDomain() }
                //Result.Success(domain)
            }
            .catch { t ->                              // Si ocurre una excepción en el flujo...
                emit(Result.Failure(t.toCoreError())) // ...la traducimos a CoreError y emitimos Failure.
            }

    /**
     * Crea una nueva tarea con título dado.
     * Devuelve Result<Unit> para comunicar éxito/fallo tipado.
     */
    override suspend fun addTask(title: String): Result<Unit> =
        withContext(dispatchers.iO) {                  // Operación de E/S: forzamos dispatchers.IO.
            runCatching {                              // Capturamos excepciones y las convertimos a Result.
                dao.upsert(TaskEntity(title = title))  // Upsert en Room: crea fila con done=false por defecto (según Entity).
            }.fold(
                onSuccess = { Result.Success(Unit) },  // OK → Success(Unit).
                onFailure = { Result.Failure(it.toCoreError()) } // Error → Failure(CoreError).
            )
        }

    /**
     * Marca/desmarca una tarea como completada.
     * - id: identificador de la tarea.
     * - completed: nuevo estado lógico en dominio (se mapea a 'done' en Entity).
     */
    override suspend fun setTaskCompleted(id: Long, completed: Boolean): com.example.taskhelper.core.common.Result<Unit> =
        withContext(dispatchers.iO) {
            try {
                // setCompleted o setDone según tu DAO
                dao.setCompleted(id, completed)
                com.example.taskhelper.core.common.Result.Success(Unit)
            } catch (t: Throwable) {
                com.example.taskhelper.core.common.Result.Failure(t.toCoreError())
            }
        }

    /**
     * Elimina una tarea por id.
     * Preferible usar un DAO con 'DELETE FROM tasks WHERE id = :id'.
     * Si tu DAO solo admite @Delete(Entity), creamos una Entity con la PK.
     */
    override suspend fun removeTask(id: Long): Result<Unit> =
        withContext(dispatchers.iO) {
            runCatching {
                // Si tienes dao.deleteById(id), usa eso en su lugar.
                dao.delete(TaskEntity(id = id, title = "", completed = false))
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Failure(it.toCoreError()) }
            )
        }

    // -----------------------
    // MAPEADORES PRIVADOS
    // -----------------------

    // Convierte la Entity de Room al modelo de dominio.
    private fun TaskEntity.toDomain(): Task =
        Task(
            id = id,                                   // PK de la fila → id de dominio.
            title = title,                             // Título persistido → título de dominio.
            completed = completed                           // 'done' (Entity) → 'completed' (dominio).
            // Si añades createdAt en dominio y no existe en Entity, puedes poner null aquí o migrar la DB.
        )

    // -----------------------
    // TRADUCCIÓN DE ERRORES
    // -----------------------

    // Mapea Throwables conocidos a un CoreError coherente para la app.
    private fun Throwable.toCoreError(): CoreError = when (this) {
        is IOException -> CoreError.Network            // Fallos de IO (disco/red) → Network (o Database si prefieres separar).
        else -> CoreError.Database                     // Para Room/SQL y otros errores por defecto → Database...
        // Puedes afinar: SQLException/RoomException → Database, resto → Unknown(this).
        // Ej.: is android.database.SQLException, is androidx.room.RoomException -> CoreError.Database
    }
}
