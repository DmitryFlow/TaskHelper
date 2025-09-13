package com.example.taskhelper.core.common

/**
 * Modelo de error común a toda la app.
 * Es 'sealed interface' para forzar que todos los tipos de error conocidos estén en este archivo,
 * permitiendo 'when' exhaustivos en compile-time y facilitando su evolución.
 */
sealed interface CoreError {

    /** Error de conectividad/red (timeouts, sin conexión, DNS, etc.). */
    data object Network : CoreError
    // 'data object' define un singleton sin estado; no necesita parámetros.

    /** Error de base de datos/persistencia (lectura/escritura, corrupción, etc.). */
    data object Database : CoreError
    // También singleton: representa un fallo genérico de Room/Storage.

    /** Recurso/entidad no encontrado (404, consultas vacías cuando se esperaba un resultado, etc.). */
    data object NotFound : CoreError
    // Útil para distinguir flujos vacíos de fallos reales.

    /** Error de validación de negocio/entrada de usuario; incluye mensaje explicativo. */
    data class Validation(val message: String) : CoreError
    // 'data class' con payload: permite comunicar el motivo concreto de la validación fallida.

    /** Error no clasificado; opcionalmente guarda la excepción original para logging. */
    data class Unknown(val throwable: Throwable? = null) : CoreError
    // Catch-all para casos que aún no modelamos; no lo muestres tal cual al usuario.
}
