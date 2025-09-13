package com.example.taskhelper.domain.task.repo

import com.example.taskhelper.core.common.Result
import com.example.taskhelper.domain.task.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * TaskRepository (DOMINIO, por feature: task)
 * ------------------------------------------
 * ¿Qué es?
 *   - Es el CONTRATO de dominio para trabajar con tareas (lectura/escritura).
 *
 * ¿Para qué sirve?
 *   - Abstrae el origen de datos (Room/Retrofit/cache) detrás de una interfaz estable.
 *   - Expone errores de forma tipada con `Result` + `CoreError` (no excepciones crudas).
 *   - Facilita testear con fakes/mocks y cambiar implementaciones sin tocar UI/UseCases.
 *
 * ¿Dónde se usa?
 *   - Lo consumen los USE CASES (p. ej., Get/Observe/Add/SetCompleted/Remove).
 *   - Indirectamente lo usa la UI a través de los ViewModels que llaman a los casos de uso.
 *   - Lo IMPLEMENTA la capa DATA (p. ej., TaskRepositoryImpl).
 */
interface TaskRepository { // Interfaz de dominio (sin dependencias Android); estable y testeable.

    /**
     * Lee/observa las tareas como flujo reactivo de dominio.
     * - Flow: emite actualizaciones (p. ej., cambios en Room).
     * - Result<List<Task>>: éxito con lista de Task de dominio; fallo con CoreError modelado.
     * Uso: llamado por GetTasksUseCase/ObserveTasksUseCase; el VM lo mapea a UiState.
     */
    fun getTasks(): Flow<Result<List<Task>>> // Lectura reactiva; la implementación decide cómo refrescar/combinar cache y red.

    /**
     * Crea una tarea con el título dado.
     * - suspend: operación de E/S.
     * - Result<Unit>: éxito/fallo tipado; evita lanzar excepciones hacia UI.
     * Uso: AddTaskUseCase -> VM -> UI.
     */
    suspend fun addTask(title: String): Result<Unit>

    /**
     * Marca o desmarca una tarea como completada.
     * - id: identificador de la tarea.
     * - completed: nuevo estado lógico.
     * Uso: SetTaskCompletedUseCase -> VM -> UI.
     */
    suspend fun setTaskCompleted(id: Long, completed: Boolean): Result<Unit>

    /**
     * Elimina la tarea por id.
     * Uso: RemoveTaskUseCase -> VM -> UI.
     */
    suspend fun removeTask(id: Long): Result<Unit>
}
