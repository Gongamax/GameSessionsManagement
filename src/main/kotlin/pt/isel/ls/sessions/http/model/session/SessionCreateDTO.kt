package pt.isel.ls.sessions.http.model.session

import kotlinx.serialization.Serializable

@Serializable
data class SessionCreateDTO(
    val gid: UInt,
    val date: String,
    val capacity: Int,
)
