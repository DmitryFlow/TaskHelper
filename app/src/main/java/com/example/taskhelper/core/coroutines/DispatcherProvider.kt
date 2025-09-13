package com.example.taskhelper.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/*
Qué es: una abstracción de Dispatchers (IO/Main/Default).
Para qué sirve: testear y mantener de forma consistente el enrutado de corutinas.
Dónde se usa: repos (IO), use cases (Default), ViewModels (Main/IO según necesidad), y se provee por DI.
 */

/** Contrato que expone los diferentes 'dispatchers' usados en la app. */
interface DispatcherProvider { // Interface para poder 'inyectar' y 'mockear' los dispatchers en tests.
    /** Dispatcher para operaciones de E/S (red, disco, Room). */
    val iO: CoroutineDispatcher // Usado para trabajo bloqueante o intensivo en I/O.

    /** Dispatcher asociado al hilo principal (UI). */
    val main: CoroutineDispatcher // Usado para actualizar estado/emitir hacia la UI.

    /** Dispatcher para trabajo CPU-bound (parseos, cálculos). */
    val default: CoroutineDispatcher // Usado para operaciones pesadas de CPU.
}

/** Implementación por defecto que delega en los dispatchers globales de Kotlin. */
class DefaultDispatcherProvider : DispatcherProvider {
    /** Usa el pool de hilos optimizado para I/O de Kotlin. */
    override val iO: CoroutineDispatcher = Dispatchers.IO // Ideal para Retrofit/Room/lectura-escritura.

    /** Usa el hilo principal de Android (Looper main). */
    override val main: CoroutineDispatcher = Dispatchers.Main // Ideal para ViewModel -> UI.

    /** Usa el pool de hilos para tareas CPU-bound. */
    override val default: CoroutineDispatcher = Dispatchers.Default // Ideal para map/transformaciones pesadas.
}
