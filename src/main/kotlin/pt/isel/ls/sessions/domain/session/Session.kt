package pt.isel.ls.sessions.domain.session

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pt.isel.ls.sessions.domain.player.Player

/**
 * Data class representing a Session.
 *
 * @property sid The unique identifier of the session.
 * @property numberOfPlayers The number of players in the session.
 * @property date The date of the session.
 * @property gid The unique identifier of the game associated with the session.
 * @property associatedPlayers The list of players associated with the session.
 */
data class Session(
    val sid: UInt,
    val numberOfPlayers: Int,
    val date: LocalDateTime,
    val gid: UInt,
    val associatedPlayers: Set<Player>,
    val capacity: Int
) {
    init {
        require(sid > 0u) { "The session id must be a positive number" }
        require(gid > 0u) { "The game id must be a positive number" }
    }
}
