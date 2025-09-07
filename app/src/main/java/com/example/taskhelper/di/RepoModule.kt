// di/RepoModule.kt
package com.example.taskhelper.di

import com.example.taskhelper.data.TaskRepositoryImpl
import com.example.taskhelper.domain.repo.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepoModule
 * ----------
 * Módulo de Hilt encargado de enlazar **interfaces de dominio** con sus
 * **implementaciones** de la capa data.
 *
 * ¿Para qué sirve?
 * - Le dice a Hilt: “cuando alguien pida `TaskRepository`, entrégale un `TaskRepositoryImpl`”.
 *
 * ¿Dónde se usa?
 * - En cualquier clase con DI (p. ej. ViewModels) que reciba `TaskRepository` por constructor:
 *     @HiltViewModel
 *     class TaskViewModel @Inject constructor(private val repo: TaskRepository) { ... }
 *   Hilt resolverá `TaskRepository` gracias a este módulo.
 */
@Module // Marca la clase como módulo de Hilt (aquí hay definiciones de dependencias)
@InstallIn(SingletonComponent::class) // Las bindings viven en el contenedor "global" de la app
abstract class RepoModule {
    /**
     * @Binds: binding de interfaz → implementación.
     * - Reglas: mét odo abstracto, un parámetro (la implementación) y el tipo de retorno es la interfaz.
     * - Hilt generará el código para usar `TaskRepositoryImpl` cada vez que se pida `TaskRepository`.
     *
     * @Singleton: una sola instancia de `TaskRepositoryImpl` para all el proceso.
     * - Útil porque suele encapsular Room/Red y no quieres duplicar recursos.
     *
     * Requiere que `TaskRepositoryImpl` tenga un constructor anotado con `@Inject`
     * o que sus dependencias estén disponibles en otros módulos.
     *
     * Alternativa:
     * - Si tu implementación necesita lógica/fábrica especial, usa @Provides en lugar de @Binds.
     */
    @Binds @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
    //     ^ impl que queremos inyectar       ^ interfaz que la app solicitará
}
