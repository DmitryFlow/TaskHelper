package com.example.taskhelper.core.common

/** Representa el resultado de una operación: éxito con datos o fallo con un error. */
sealed class Result<out T> { // 'sealed' restringe las subclases al mismo archivo. 'out T' hace covariante al tipo (Result<Hijo> es Result<Padre>).

    /** Caso de éxito: contiene el valor producido por la operación. */
    data class Success<T>(val data: T) : Result<T>() // 'data class' para igualdad por valor; guarda el payload 'data'.

    /** Caso de fallo: contiene un error de dominio común. */
    data class Failure(val error: CoreError) : Result<Nothing>()
    // Hereda de Result<Nothing> para que Failure sea asignable a Result<T> de cualquier T (Nothing es subtipo de todos los tipos).
}

/**
 * Transforma el valor interno de un Result en caso de éxito, manteniendo el fallo tal cual.
 *
 * @param transform función que convierte el valor de tipo T a otro de tipo R.
 * @return Result<R> con Success(transform(data)) o el mismo Failure.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    // 'inline' evita la sobrecarga de la lambda en llamadas intensivas (mejor rendimiento).
    // Extensión sobre Result<T>; usa genéricos T (entrada) y R (salida).

    is Result.Success -> Result.Success(transform(data))
    // Si hay éxito, aplica la transformación al 'data' y envuelve en otro Success con el nuevo tipo R.

    is Result.Failure -> this
    // Si hay fallo, lo propagamos sin tocar (se preserva el error original).
}
