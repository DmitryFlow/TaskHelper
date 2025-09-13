package com.example.taskhelper.data.task.remote

import retrofit2.http.GET

// Interfaz de Retrofit que declara los endpoints remotos relacionados con "tasks".
// Retrofit generará la implementación de esta interfaz en runtime a partir de las anotaciones.
interface TaskApi {

    // Anotación HTTP que indica una petición GET a la ruta relativa "tasks".
    // Se concatenará con la baseUrl configurada en Retrofit (p. ej., "https://api.example.com/"),
    // resultando en GET https://api.example.com/tasks
    @GET("tasks")
    // Función "suspend" porque se ejecuta de forma asíncrona con coroutines.
    // Devuelve la lista de DTOs de red que representan tareas tal y como las expone el backend.
    // Donde se usa: inyectada en TaskRepositoryImpl, que mapeará List<TaskDto> → dominio/entidad.
    // Errores: si hay fallo de red o parseo, lanzará excepción que el repo debe capturar y traducir a CoreError.
    suspend fun getTasks(): List<TaskDto>
}
