package pt.isel.ls.sessions.http.routes

import org.http4k.core.Request

private fun Request.getBearToken(): String? {
    val authHeader = header("Authorization") ?: return null
    val parts = authHeader.split(" ")
    if (parts.size != 2 || parts[0] != "Bearer") {
        return null
    }
    return parts[1]
}

fun Request.bearerTokenOrThrow(): String =
    getBearToken() ?: throw IllegalArgumentException("No bearer token found")

fun Request.authenticated(): Boolean = getBearToken() != null