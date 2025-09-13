package com.example.taskhelper.domain.task.usecase

import com.example.taskhelper.core.common.Result
import com.example.taskhelper.domain.task.model.Task
import com.example.taskhelper.domain.task.repo.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso de dominio para obtener la lista de tareas.
 * - Define un punto de entrada único para la UI/otros casos de uso.
 * - Devuelve un Flow para soportar actualizaciones en tiempo real (e.g., Room).
 * - Envuelve la respuesta en Result para diferenciar éxito/fallo sin lanzar excepciones hacia UI.
 * - No conoce detalles de datos (ni Retrofit ni Room): delega en TaskRepository (contrato de domain).
 */
class GetTasksUseCase @Inject constructor(          // Clase de caso de uso; '@Inject' permite a Hilt construirla automáticamente.
    private val repository: TaskRepository           // Dependencia del contrato de repositorio (interfaz en 'domain'); facilita test con fakes.
) {
    /**
     * Operador 'invoke' para que el caso de uso se use como función: getTasksUseCase()
     * @return Flow<Result<List<Task>>> flujo reactivo con éxito (lista de Task) o fallo (CoreError en Result.Failure).
     *
     * Se delega en el repositorio, que decide cómo combinar fuentes (red+cache) y mapear a modelos de dominio.
     * No cambia de dispatcher aquí: el trabajo IO/CPU se maneja en 'data' o en use cases más complejos.
     */
    operator fun invoke(): Flow<Result<List<Task>>> = repository.getTasks() // Delegación directa: mantiene el caso de uso fino y testeable.
}
