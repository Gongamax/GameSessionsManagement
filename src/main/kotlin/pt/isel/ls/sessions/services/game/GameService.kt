package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.data.AppMemoryDB

class GameService(private val memoryDB: AppMemoryDB) {
    fun getGame(gid: Int, limit: Int = 0, skip: Int = 0) = memoryDB.gameMemoryDB.getGameById(gid)
    fun getGames(
        genres: List<Genres>,
        developer: String,
        limit: Int = 0,
        skip: Int = 0
    ) = memoryDB.gameMemoryDB.getGames(genres, developer)
    fun createGame(name: String, developer: String, genres: List<Genres>) = memoryDB.gameMemoryDB.createGame(name, developer, genres)

}