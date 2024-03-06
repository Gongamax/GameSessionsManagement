package pt.isel.ls.sessions.repository.data.player

import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.utils.Token
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


class PlayerMemoryDB() : PlayerDB {
    private val playersMap = ConcurrentHashMap<Int, Player>()
    private var nextPlayerId = AtomicInteger(1)


    override fun createPlayer(name: String, email: String): Token {
        if (playersMap.any { it.value.email == email })
            throw IllegalArgumentException("Email already exists.")
        val pid = nextPlayerId.getAndIncrement()
        val player = Player(pid, name, email)
        playersMap[pid] = player
        val token = UUID.randomUUID().toString()
        return Token(pid, token)
    }

    override fun getPlayers() = playersMap.map { it.value }

    override fun getPlayerById(pid: Int) = playersMap[pid]

    override fun reset() {
        playersMap.clear()
        nextPlayerId = AtomicInteger(1)
    }

    override fun isEmailInUse(email: String): Boolean {
        TODO("Not yet implemented")
    }

}

