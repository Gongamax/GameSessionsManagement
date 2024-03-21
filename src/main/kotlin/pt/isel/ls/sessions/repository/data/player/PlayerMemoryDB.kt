package pt.isel.ls.sessions.repository.data.player

import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.utils.Token
import pt.isel.ls.sessions.repository.PlayerRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class PlayerMemoryDB : PlayerRepository {
    private val playersMap = ConcurrentHashMap<UInt, Player>()
    private var nextPlayerId = AtomicInteger(1)

    override fun createPlayer(name: String, email: String): Token {
        val pid = nextPlayerId.getAndIncrement().toUInt()
        val player = Player(pid, name, email)
        playersMap[pid] = player
        val token = UUID.randomUUID()
        return Token(pid, token)
    }

    override fun getPlayers() = playersMap.map { it.value }

    override fun getPlayerById(pid: UInt) = playersMap[pid]

    override fun reset() {
        playersMap.clear()
        nextPlayerId = AtomicInteger(1)
    }

    override fun isEmailInUse(email: String): Boolean = playersMap.any { it.value.email == email }
}

