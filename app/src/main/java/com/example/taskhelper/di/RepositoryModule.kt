package com.example.taskhelper.di

import com.example.taskhelper.data.task.TaskRepositoryImpl
import com.example.taskhelper.domain.task.repo.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ¿Qué es, para qué sirve y dónde se usa?
 * - Módulo de Hilt que enlaza la interfaz de dominio `TaskRepository` con su implementación de data `TaskRepositoryImpl`.
 * - ¿Para qué sirve? Permite que, cuando cualquier clase pida `TaskRepository`, Hilt inyecte automáticamente un `TaskRepositoryImpl`.
 * - ¿Dónde se usa? En ViewModels y UseCases que dependen del contrato de dominio; en tests puedes sustituir este binding por uno fake.
 */
@Module // Declara esta clase como un módulo de Hilt: aquí definimos cómo resolver dependencias.
@InstallIn(SingletonComponent::class) // Este módulo vive en el grafo de aplicación (una sola instancia por proceso).
abstract class RepositoryModule { // 'abstract' porque usaremos @Binds (requiere métodos abstractos).

    /**
     * Binding interfaz → implementación.
     *
     * @Binds: indica a Hilt que, cuando se requiera el tipo de retorno (TaskRepository),
     *         debe proporcionar la instancia pasada como parámetro (TaskRepositoryImpl).
     *
     * @Singleton: una única instancia compartida durante todo el ciclo de vida de la app.
     *             Útil porque el repo suele encapsular recursos (Room/OkHttp) que no conviene duplicar.
     *
     * Requisitos:
     * - `TaskRepositoryImpl` debe tener constructor con `@Inject` y sus dependencias (TaskDao, TaskApi, etc.)
     *   deben estar provistas por otros módulos (DatabaseModule/NetworkModule/DispatcherModule).
     *
     * Alternativas:
     * - Si necesitas lógica de construcción más compleja, usa @Provides en un objeto en lugar de @Binds.
     * - En tests, puedes reemplazar este binding con @TestInstallIn y un fake repo.
     */
    @Binds @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
    //                             ^ implementación concreta en data         ^ contrato expuesto en domain
}
