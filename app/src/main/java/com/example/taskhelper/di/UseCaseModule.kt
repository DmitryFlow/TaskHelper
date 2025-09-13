package com.example.taskhelper.di

import com.example.taskhelper.domain.task.repo.TaskRepository
import com.example.taskhelper.domain.task.usecase.GetTasksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/*
Para qué sirve: centraliza la creación de use cases y declara explícitamente de qué contratos de dominio
dependen (p. ej., TaskRepository).

Dónde se usa: Hilt lo usa para inyectar use cases en ViewModels u otros use cases.

Nota: si tu GetTasksUseCase tiene @Inject constructor(...), este @Provides es opcional; Hilt podría construirlo
automáticamente. Mantener el módulo es útil si quieres controlar el scope, añadir parámetros adicionales o agrupar
todos los use cases en un mismo lugar.
 */

@Module // Declara esta clase/objeto como un "módulo" de Hilt: aquí definimos cómo se crean ciertas dependencias.
@InstallIn(SingletonComponent::class) // Este módulo vive en el grafo de aplicación (disponible en toda la app).
object UseCaseModule { // 'object' para métodos estáticos @Provides (no hace falta instanciar la clase).

    @Provides // Indica a Hilt que esta función "provee" (construye) una dependencia inyectable.
    fun provideGetTasksUseCase( // Nombre descriptivo del proveedor: devuelve un GetTasksUseCase listo para inyectar.
        repo: TaskRepository    // Dependencia requerida por el use case. Hilt la resolverá via el binding interfaz→impl.
    ): GetTasksUseCase =        // Tipo de retorno explícito: el caso de uso que exponemos al grafo de DI.
        GetTasksUseCase(repo)   // Construcción del caso de uso inyectando el repositorio de dominio.
    // Scope: sin anotación @Singleton → Hilt puede crear nuevas instancias cuando se soliciten.
    // Si quisieras compartir una única instancia, añade @Singleton sobre el @Provides (suele no ser necesario en use cases).
}
