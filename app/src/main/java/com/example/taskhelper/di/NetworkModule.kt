package com.example.taskhelper.di

import com.example.taskhelper.data.task.remote.TaskApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * ¿Qué es, para qué sirve y dónde se usa?
 * - Módulo de Hilt que configura la pila de red basada en Moshi + Retrofit + OkHttp.
 * - ¿Para qué sirve? Centraliza la creación de dependencias de red (singletons), evita duplicación y facilita tests/mocks.
 * - ¿Dónde se usa? La capa data (p. ej., TaskRepositoryImpl) inyecta TaskApi para realizar llamadas al backend.
 */
@Module // Declara que esta clase define cómo se crean ciertas dependencias para el grafo de DI.
@InstallIn(SingletonComponent::class) // Tod0 lo provisto vive en el scope de aplicación (una instancia global).
object NetworkModule { // Objeto estático: no requiere instanciación manual.

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient = // Proveedor del cliente HTTP subyacente (conexiones, interceptores, timeouts).
        OkHttpClient.Builder()
            // .addInterceptor(loggingInterceptor) // (Opcional) Logging solo en debug.
            // .connectTimeout(10, TimeUnit.SECONDS) // (Opcional) Ajusta timeouts según tu backend/red.
            // .readTimeout(30, TimeUnit.SECONDS)
            .build() // Instancia final compartida como singleton.

    @Provides @Singleton
    fun provideMoshi(): Moshi = // Proveedor del motor de (de)serialización JSON Moshi.
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // Habilita soporte reflejado para data classes Kotlin (sin codegen/kapt).
            .build() // Instancia lista; si usas codegen en el futuro, puedes quitar este factory.

    @Provides @Singleton
    fun provideRetrofit( // Proveedor de Retrofit, que orquesta peticiones HTTP y convierte JSON ↔ DTOs con Moshi.
        client: OkHttpClient, // Reutilizamos el OkHttpClient singleton de arriba.
        moshi: Moshi          // Motor Moshi que hemos configurado (con soporte Kotlin).
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.example.com/") // TODO: mover a BuildConfig.BASE_URL por flavor/env (dev/staging/prod).
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Conector Retrofit ↔ Moshi.
            .client(client) // Reutiliza el cliente HTTP configurado (interceptores, timeouts, etc.).
            .build() // Instancia de Retrofit lista para crear APIs.

    @Provides @Singleton
    fun provideTaskApi(retrofit: Retrofit): TaskApi =
        retrofit.create() // Genera implementación dinámica de la interfaz TaskApi (@GET/@POST).
    // ¿Dónde se usa? Inyectada en repositorios de data (TaskRepositoryImpl) para llamadas remotas.
}
