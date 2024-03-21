package pt.isel.ls.sessions.http.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.path

const val CONTENT_TYPE = "content-type"
const val LOCATION = "Location"
const val APPLICATION_JSON = "application/json"
const val ACCEPT = "accept"

inline fun <reified T> Response.jsonResponse(
    content: T,
    type: String = APPLICATION_JSON,
): Response =
    header(CONTENT_TYPE, type)
        .body(Json.encodeToString(content))

// TODO: MAYBE CREATE CUSTOM EXCEPTION FOR THIS CASE
internal fun Request.getPathSegments(vararg list: String): List<String> =
    list.map {
        val value = path(it)
        require(value != null) {
            "Path segment {$value} could not be retrieved."
        }
        value
    }
