// app/src/main/java/com/example/taskhelper/data/remote/ApiService.kt
package com.example.taskhelper.data.remote

import retrofit2.http.GET

/** Interfaz de Retrofit que define los endpoints HTTP del backend. */
interface ApiService {
    /** Endpoint de salud del backend. */
    @GET("ping")
    suspend fun ping(): Map<String, Any>
}
