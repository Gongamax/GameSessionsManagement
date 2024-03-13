package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.toGenre
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

class GameService(private val memoryDB: AppMemoryDB) {
    fun getGame(gid: Int): GameGetByIdResult = run {
        val game = memoryDB.gameMemoryDB.getGameById(gid)
        if (game != null) success(game)
        else failure(GameGetError.GameNotFound)
    }

    fun getGames(
        genres: List<String>, developer: String, limit: Int, skip: Int
    ): GamesGetResult = run {
        val gen = genres.mapNotNull { it.toGenre() }
        if (gen.size < genres.size)
            return failure(GamesGetError.GenreNotFound)
        val games = memoryDB.gameMemoryDB.getGames(gen, developer)
        if (games.isNotEmpty()) return@run success(games)
        val dev = memoryDB.gameMemoryDB.getDeveloperByName(developer)
        when(dev) {
            null -> failure(GamesGetError.DeveloperNotFound)
            else -> failure(GamesGetError.NoGamesFound)
        }
    }


    fun createGame(name: String, developer: String, genres: List<String>): GameCreationResult = run {
        val gen = genres.mapNotNull { it.toGenre() }
        if (gen.size < genres.size)
            return failure(GameCreationError.InvalidGenre)
        val gid = memoryDB.gameMemoryDB.createGame(name, developer, gen)
        if (gid != null) success(gid)
        else failure(GameCreationError.NameAlreadyExists)
    }

}