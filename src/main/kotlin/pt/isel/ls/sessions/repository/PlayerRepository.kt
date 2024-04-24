package pt.isel.ls.sessions.repository

import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.utils.Token

interface PlayerRepository {
    fun createPlayer(
        name: String,
        email: String,
    ): Token

    fun getPlayers(): List<Player>

    fun getPlayerById(pid: UInt): Player?

    fun reset()

    fun isEmailInUse(email: String): Boolean

    fun isNameInUse(name: String): Boolean
}
