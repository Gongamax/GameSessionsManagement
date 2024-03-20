package pt.isel.ls.sessions.http.model.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.sessions.http.model.player.PlayerDTO

@Serializable
data class SessionDTO(
    val sid: UInt,
    val numberOfPlayers: Int,
    val date: LocalDateTime,
    val gid: UInt,
    val associatedPlayers: List<PlayerDTO>,
    val capacity: Int
)


