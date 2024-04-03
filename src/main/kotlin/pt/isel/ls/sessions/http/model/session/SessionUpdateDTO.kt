package pt.isel.ls.sessions.http.model.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class SessionUpdateDTO(
    val capacity: Int,
    val date: LocalDateTime
)
