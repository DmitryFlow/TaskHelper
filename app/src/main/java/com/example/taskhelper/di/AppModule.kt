// di/AppModule.kt
package com.example.taskhelper.di

import android.content.Context
import com.example.taskhelper.BuildConfig
import com.example.taskhelper.data.prefs.UserPrefs
import com.example.taskhelper.data.remote.ApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Módulo de Hilt que declara **cómo construir** dependencias compartidas de la app:
 * red (OkHttp/Retrofit/Moshi), base de datos (Room), preferencias (DataStore) y API service.
 *
 * - @Module: le dice a Hilt que aquí hay "recetas" para crear objetos.
 * - @InstallIn(SingletonComponent::class): all lo que se provee aquí vive en el scope **Singleton**,
 *   es decir, una sola instancia para all el proceso de la app.
 *
 * ¿Dónde se usa lo que se crea aquí?
 * - Repositorios (capa data) inyectan ApiService, AppDatabase/Dao y UserPrefs.
 * - ViewModels indirectamente consumen estos repos vía DI.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Crea el cliente HTTP que usará Retrofit.
     * - Marcado como @Singleton: una única instancia en toda la app (ahorra sockets y pools).
     * - Añadimos un interceptor de logs para ver las peticiones/respuestas (útil en debug).
     *
     * DÓNDE SE USA:
     * - inyectado en provideRetrofit().
     * - indirectamente lo usarán todos los repos que vayan contra red (a través de Retrofit).
     *
     * NOTA: puedes condicionar el nivel de logs a un flag de BuildConfig para que solo
     * loguee en debug (ver comentario dentro).
     */
    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient
            .Builder()
            .apply {
                // logging en debug
                // Usa BuildConfig.ENABLE_HTTP_LOGS si quieres condicionar esto
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            }.build()

    /**
     * Crea el objeto Moshi (serializador JSON).
     * - Con moshi-kotlin en dependencias, Moshi entiende data classes de Kotlin.
     * - Con codegen (ksp) evitas reflexión y ganas rendimiento.
     *
     * DÓNDE SE USA:
     * - Se inyecta en MoshiConverterFactory de Retrofit para convertir JSON <-> modelos.
     */
    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    /**
     * Construye Retrofit con:
     * - baseUrl: dominio de tu backend (mueve esto a BuildConfig para separar debug/release).
     * - OkHttp: el cliente anterior, compartido.
     * - MoshiConverterFactory: para parsear JSON con Moshi.
     *
     * DÓNDE SE USA:
     * - Se inyecta en provideApi() para crear la interfaz de tu API.
     */
    @Provides @Singleton
    fun provideRetrofit(
        okHttp: OkHttpClient,
        moshi: Moshi,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /**
     * Crea la implementación de tu servicio de red a partir de la interfaz ApiService.
     * Retrofit genera el proxy en tiempo de ejecución.
     *
     * DÓNDE SE USA:
     * - Inyectado en repositorios remotos (por ejemplo, TaskRemoteDataSource o cualquier Repo
     *   que golpee endpoints). El repo lo usará para hacer llamadas: api.ping(), api.getTasks(), etc.
     */
    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    /**
     * Crea el wrapper de DataStore para preferencias de usuario.
     * - @Singleton: un único access point para preferencias.
     *
     * DÓNDE SE USA:
     * - Inyectado en repos de settings/feature flags/tema oscuro, etc.
     * - Cualquier clase que necesite leer/escribir preferencias reactivas.
     */
    @Provides @Singleton
    fun provideUserPrefs(
        @ApplicationContext ctx: Context,
    ) = UserPrefs(ctx)
}
