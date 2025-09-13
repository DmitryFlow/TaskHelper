package com.example.taskhelper.data.task.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable // Habilita a kotlinx-serialization para (de)serializar esta clase a/desde JSON sin reflexión.
data class TaskDto( // DTO de red: representa exactamente el JSON del backend para una "task".
    @SerialName("id") val id: Long,         // Campo "id" del JSON → identificador de la tarea.
    @SerialName("title") val title: String, // Campo "title" del JSON → título de la tarea.
    @SerialName("completed") val completed: Boolean, // Campo "completed" del JSON → estado lógico en el backend.
    // Cadena ISO-8601 o null; p. ej. "2025-09-12T18:30:00Z". Se parsea en el mapper (Instant.parse) si necesitas fecha.
    @SerialName("created_at") val createdAt: String? = null // Valor por defecto null: si el server no lo envía, no falla la deserialización.
)
