package com.example.taskhelper.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskhelper.core.common.Result
import com.example.taskhelper.domain.task.model.Task
import com.example.taskhelper.domain.task.usecase.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/*
Para qué sirve: orquesta el flujo de datos desde el caso de uso GetTasksUseCase hacia la UI, exponiendo un
StateFlow<TaskUiState> inmutable que la pantalla (Compose) puede observar de forma segura. Gestiona carga,
datos y errores sin bloquear el hilo principal.

Dónde se usa: lo consume la pantalla HomeScreen (u otras pantallas de tareas) mediante hiltViewModel() y
collectAsStateWithLifecycle(). Se inyecta con Hilt y no conoce detalles de la capa data (Room/Retrofit): solo
usa GetTasksUseCase.
 */

// Estado inmutable que la UI observará para representarse.
data class TaskUiState(
    val isLoading: Boolean = false,          // Bandera de carga: true mientras esperamos datos.
    val tasks: List<Task> = emptyList(),     // Lista de tareas de dominio lista para pintar en Compose.
    val error: String? = null                // Texto de error (null si no hay error).
) {
    companion object {
        // Creador de UiState a partir del Result de dominio (éxito/fallo).
        fun from(result: Result<List<Task>>) = when (result) {
            is Result.Success -> TaskUiState(tasks = result.data) // En éxito: poblamos 'tasks'.
            is Result.Failure -> TaskUiState(error = result.error.toString()) // En fallo: guardamos el error para mostrar.
        }
    }
}

// Anotación de Hilt: permite inyección automática de dependencias en este ViewModel.
@HiltViewModel
class TaskViewModel @Inject constructor(      // Constructor inyectado: Hilt provee el caso de uso.
    getTasks: GetTasksUseCase                 // Dependencia: caso de uso que expone Flow<Result<List<Task>>>.
) : ViewModel() {

    // Estado expuesto a la UI como StateFlow para interoperar bien con Compose.
    val state: StateFlow<TaskUiState> = getTasks()   // Llama al use case (operator invoke) → Flow<Result<List<Task>>>.
        .map { TaskUiState.from(it) }                // Transforma Result<List<Task>> → TaskUiState (éxito/fallo).
        .onStart { emit(TaskUiState(isLoading = true)) } // Antes de la 1ª emisión real: muestra 'loading'.
        .stateIn(                                    // Convierte el Flow en StateFlow con alcance del ViewModel.
            scope = viewModelScope,                  // Alcance atado al ciclo de vida del ViewModel.
            started = SharingStarted.WhileSubscribed(5_000), // Comparte mientras haya suscriptores; espera 5s antes de parar upstream.
            initialValue = TaskUiState(isLoading = true)     // Valor inicial inmediato para la UI (loading).
        )
}
