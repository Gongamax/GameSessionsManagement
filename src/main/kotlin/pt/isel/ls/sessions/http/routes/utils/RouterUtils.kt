package pt.isel.ls.sessions.http.routes.utils

import org.http4k.core.Request

data class TokenNotFoundException(override val message: String) : IllegalArgumentException(message)

private fun Request.getBearToken(): String? {
    val authHeader = header("Authorization") ?: return null
    val parts = authHeader.split(" ")
    if (parts.size != 2 || parts[0] != "Bearer") {
        return null
    }
    return parts[1]
}

fun Request.bearerTokenOrThrow(): String = getBearToken() ?: throw TokenNotFoundException("No bearer token found")
