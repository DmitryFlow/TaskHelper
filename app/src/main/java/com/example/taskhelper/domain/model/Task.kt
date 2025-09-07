// domain/model/Task.kt
package com.example.taskhelper.domain.model

// 'data class' define un POJO inmutable con equals()/hashCode()/toString()/copy() generados.
// Este es el **modelo de dominio** de una tarea, el que consumen ViewModels, UseCases y la UI.
// Lo mantenemos separado de la Entity de Room (TaskEntity) para no acoplar dominio a la capa de datos.
data class Task(
    val id: Long,        // Identificador único de la tarea (coincidirá con la PK en DB, pero aquí es agnóstico).
    val title: String,   // Título visible en la UI y en lógica de negocio.
    val done: Boolean    // Estado de completado que la UI mostrará (checkbox, estilo tachado, etc.).
)
