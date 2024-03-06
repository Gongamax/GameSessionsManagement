package pt.isel.ls.sessions.repository.data.player

import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.utils.Token
import pt.isel.ls.sessions.domain.utils.TokenValidationInfo
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import java.util.UUID


class PlayerMemoryDB(private val source: AppMemoryDB) : PlayerDB {

    override fun createPlayer(name: String, email: String): Token {
        if (source.playersMap.any { it.value.email == email })
            throw IllegalArgumentException("Email already exists.")
        val pid = source.nextPlayerId.getAndIncrement()
        val player = Player(pid, name, email)
        source.playersMap[pid] = player
        val token = UUID.randomUUID().toString()
        return Token(pid, token)
    }

    override fun getPlayers() = source.playersMap.map { it.value }

    override fun getPlayerById(pid: Int) = source.playersMap[pid]

    override fun reset() {
        source.reset()
    }

}

