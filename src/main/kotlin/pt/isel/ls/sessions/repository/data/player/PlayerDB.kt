package pt.isel.ls.sessions.repository.data.player

import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.utils.Token

interface PlayerDB {

    fun createPlayer(name: String, email: String): Token

    fun getPlayers(): List<Player>

    fun getPlayerById(pid: Int): Player?

    fun reset()

    fun isEmailInUse(email : String) : Boolean

}