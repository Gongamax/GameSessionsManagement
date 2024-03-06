package pt.isel.ls.sessions.repository.data

import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class AppMemoryDB {
    val playersMap = ConcurrentHashMap<Int, Player>()
    var nextPlayerId = AtomicInteger(1)


     fun reset() {
        playersMap.clear()
        nextPlayerId = AtomicInteger(1)
    }

    val playerMemoryDB = PlayerMemoryDB(this)
}