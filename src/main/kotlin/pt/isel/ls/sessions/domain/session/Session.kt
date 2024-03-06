package pt.isel.ls.sessions.domain.session

import pt.isel.ls.sessions.domain.player.Player
import java.util.Date

/**
 * Data class representing a Session.
 *
 * @property sid The unique identifier of the session.
 * @property numberOfPlayers The number of players in the session.
 * @property date The date of the session.
 * @property gid The unique identifier of the game associated with the session.
 * @property associatedPlayers The list of players associated with the session.
 */
data class Session(val sid: Int, val numberOfPlayers: Int, val date: Date, val gid: Int, val  associatedPlayers: List<Player>)
