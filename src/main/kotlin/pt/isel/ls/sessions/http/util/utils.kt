package pt.isel.ls.sessions.http.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Response

const val CONTENT_TYPE = "content-type"
const val APPLICATION_JSON = "application/json"
const val ACCEPT = "accept"

inline fun <reified T> Response.json(content : T) : Response =
    header(CONTENT_TYPE, APPLICATION_JSON)
        .body(Json.encodeToString(content))