package pt.isel.ls.sessions.repository.data.game

import pt.isel.ls.sessions.domain.game.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class GameMemoryDB : GameDB {

    private val gameMap = ConcurrentHashMap<Int, Game>()
    private var nextGameId = AtomicInteger(1)

    override fun createGame(name: String, developer: String, genres: List<Genres>): Int? =
        if (gameMap.any { it.value.name == name })
            null
        else {
            val gid = nextGameId.getAndIncrement()
            gameMap[gid] = Game(gid, name, developer, genres)
            gid
        }

    override fun getGames(genres: List<Genres>, developer: String): List<Game> = gameMap.values.filter { game ->
        genres.all { genre -> game.genres.contains(genre) }
    }.filter { game -> game.developer == developer }.toList()


    override fun getGameById(gid: Int): Game? = gameMap[gid]

    override fun reset(): Unit = run {
        gameMap.clear()
        nextGameId = AtomicInteger(1)
    }

}