package pt.isel.ls.sessions.repository.data.game

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.GameRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class GameMemoryDB : GameRepository {
    private val gameMap = ConcurrentHashMap<UInt, Game>()
    private var nextGameId = AtomicInteger(1)

    override fun createGame(
        name: String,
        developer: String,
        genres: List<Genres>,
    ): UInt {
        val gid = nextGameId.getAndIncrement().toUInt()
        gameMap[gid] = Game(gid, name, developer, genres)
        return gid
    }

    override fun getGames(
        genres: List<Genres>,
        developer: String,
        limit: Int,
        skip: Int,
    ): List<Game> =
        gameMap.values.filter { game ->
            (developer.isBlank() || game.developer == developer) &&
                (genres.isEmpty() || game.genres.containsAll(genres))
        }.drop(skip).take(limit)

    override fun searchGamesByName(
        name: String,
        limit: Int,
        skip: Int,
    ): List<Game> = gameMap.values.filter { it.name.contains(name, ignoreCase = true) }.drop(skip).take(limit)

    override fun getGameById(gid: UInt): Game? = gameMap[gid]

    override fun reset() {
        gameMap.clear()
        nextGameId = AtomicInteger(1)
    }

    override fun getDeveloperByName(developer: String): String? = gameMap.values.find { it.developer == developer }?.developer

    override fun getGameByName(name: String): Boolean = gameMap.any { it.value.name == name }
}
