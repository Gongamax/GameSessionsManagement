package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

class GameService(private val memoryDB: AppMemoryDB) {
    fun getGame(gid: Int): GameGetByIdResult = run {
        val game = memoryDB.gameMemoryDB.getGameById(gid)
        if (game != null) success(game)
        else failure(GameGetByIdError.GameNotFound)
    }

    fun getGames(
        genres: List<Genres>, developer: String, limit: Int, skip: Int
    ) = memoryDB.gameMemoryDB.getGames(genres, developer)

    fun createGame(name: String, developer: String, genres: List<Genres>): GameCreationResult = run {
        val gid = memoryDB.gameMemoryDB.createGame(name, developer, genres)
        if (gid != null) success(gid)
        else failure(GameCreationError.NameAlreadyExists)
    }

}