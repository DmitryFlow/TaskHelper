// data/remote/ApiService.kt
package com.example.taskhelper.data.remote

import retrofit2.http.GET

/*
 * ApiService
 * ----------
 * Interfaz de Retrofit que define los endpoints HTTP de tu backend.
 * Retrofit genera en tiempo de ejecución una implementación "proxy" de esta interfaz.
 *
 * ¿Dónde se usa?
 * - Se crea en DI (AppModule.provideApi(retrofit)) con: retrofit.create(ApiService::class.java)
 * - Se inyecta en repositorios remotos para hacer llamadas de red.
 */

interface ApiService {
    // Declara un endpoint GET a la ruta relativa "ping".
    // Se concatenará a la baseUrl configurada en Retrofit (p. ej., BuildConfig.API_BASE_URL + "ping").
    // Importante: la baseUrl debe terminar en "/" para que la concatenación sea válida.
    @GET("ping")

    // 'suspend': la llamada es asíncrona con coroutines (no bloquea el hilo principal).
    // Retrofit + Moshi convertirán la respuesta JSON a un Map<String, Any>.
    // Nota: usar Map es "rápido" pero NO es tipado; mejor definir un DTO (data class) para mayor seguridad.
    // Ejemplo recomendado:
    // data class PingDto(val status: String)
    // suspend fun ping(): PingDto
    suspend fun ping(): Map<String, Any>
}
