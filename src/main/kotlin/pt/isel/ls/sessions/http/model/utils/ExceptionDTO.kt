package pt.isel.ls.sessions.http.model.utils

import kotlinx.serialization.Serializable


/**
 * Marks a class as serializable.
 * This annotation is used by the Kotlin serialization library to indicate that a class can be serialized to and deserialized from a binary or text representation.
 */
@Serializable
data class ExceptionDTO(val message: String)