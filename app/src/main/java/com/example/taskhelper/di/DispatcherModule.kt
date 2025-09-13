package com.example.taskhelper.di

import com.example.taskhelper.core.coroutines.DefaultDispatcherProvider
import com.example.taskhelper.core.coroutines.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
Para qué sirve: centraliza la entrega de los dispatchers de coroutines (IO, Main, Default) mediante la abstracción
DispatcherProvider. Esto mejora testabilidad (en tests puedes sustituirlo por un TestDispatcherProvider) y
consistencia (todas las capas usan los mismos dispatchers).

Dónde se usa: lo inyectan principalmente la capa data (repositorios) y, ocasionalmente, domain (use cases) para
cambiar de contexto de ejecución (withContext(dispatchers.IO), etc.). Hilt mantiene una única instancia (singleton)
para tod0 el proceso.
 */

@Module // Declara esta clase/objeto como un módulo de Hilt (aquí definimos cómo se crean/proveen dependencias).
@InstallIn(SingletonComponent::class) // Indica que los bindings viven en el grafo de aplicación (scope global/singleton).
object DispatcherModule { // Objeto estático: no necesita instanciarse; Hilt invoca sus @Provides cuando hace falta.

    @Provides // Marca el métod0 como proveedor de una dependencia que Hilt puede inyectar.
    @Singleton // Una sola instancia compartida en tod0 el proceso: suficiente para un provider inmutable.
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
    // ^ Tipo de retorno expuesto al grafo (la abstracción)            ^ Implementación concreta que envuelve Dispatchers.IO/Main/Default
    // ¿Qué retorna? Un DispatcherProvider que mapea a los dispatchers de Kotlin:
    //   - IO: para E/S (Room, Retrofit)
    //   - Main: para actualizar UI (ViewModel → Compose)
    //   - Default: para trabajo CPU-bound (parsing/cálculos)
    // ¿Por qué así? Inyectar la abstracción permite que en tests se reemplace por un TestDispatcherProvider
    // (p. ej., con @TestInstallIn y un módulo que 'replaces = [DispatcherModule::class]').
}
