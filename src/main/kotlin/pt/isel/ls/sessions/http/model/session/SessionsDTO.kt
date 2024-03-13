package pt.isel.ls.sessions.http.model.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.http.model.player.PlayerDTO

@Serializable
data class SessionsDTO(
    val sid: Int,
    val numberOfPlayers: Int,
    val date: LocalDateTime,
    val gid: Int,
    val associatedPlayers: List<PlayerDTO>,
    val capacity: Int
)


