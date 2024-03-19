package pt.isel.ls.sessions.http.model.session

import kotlinx.serialization.Serializable

@Serializable
data class SessionDTO(
    val capacity: Int,
    val gid: UInt,
    val date: String
)
