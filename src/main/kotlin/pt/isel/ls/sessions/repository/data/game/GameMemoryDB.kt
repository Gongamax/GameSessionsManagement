package pt.isel.ls.sessions.repository.data.game

import pt.isel.ls.sessions.domain.game.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class GameMemoryDB : GameDB {

    private val gameMap = ConcurrentHashMap<UInt, Game>()
    private var nextGameId = AtomicInteger(1)

    override fun createGame(name: String, developer: String, genres: List<Genres>): UInt? =
        if (gameMap.any { it.value.name == name })
            null
        else {
            val gid = nextGameId.getAndIncrement().toUInt()
            gameMap[gid] = Game(gid, name, developer, genres)
            gid
        }

    override fun getGames(genres: List<Genres>, developer: String): List<Game> = gameMap.values.filter { game ->
        genres.any {  genre -> game.genres.contains(genre) }
    }.filter { game -> game.developer == developer }.toList()


    override fun getGameById(gid: UInt): Game? = gameMap[gid]

    override fun reset(): Unit = run {
        gameMap.clear()
        nextGameId = AtomicInteger(1)
    }

    override fun getDeveloperByName(developer: String): String? =
        gameMap.values.find { it.developer == developer }?.developer

}