package pt.isel.ls.sessions.http.model.session

import kotlinx.serialization.Serializable

@Serializable
data class SessionsDTO(
    val sessions: List<SessionDTO>,
)
