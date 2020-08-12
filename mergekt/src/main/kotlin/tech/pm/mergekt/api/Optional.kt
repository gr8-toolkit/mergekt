package tech.pm.mergekt.api

sealed class Optional<out T> {
    object NoData : Optional<Nothing>()
    data class Data<T>(val value: T) : Optional<T>()
}
