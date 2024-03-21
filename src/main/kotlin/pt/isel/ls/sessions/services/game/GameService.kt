package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.toGenre
import pt.isel.ls.sessions.repository.AppDB
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

class GameService(private val memoryDB: AppDB) {
    fun getGame(gid: UInt): GameGetByIdResult = run {
        val game = memoryDB.gameDB.getGameById(gid)
        if (game != null) success(game)
        else failure(GameGetError.GameNotFound)
    }

    fun getGames(genres: List<String>, developer: String, limit: Int, skip: Int): GamesGetResult = run {
        val gen = genres.mapNotNull { it.toGenre() }
        if (gen.size != genres.size)
            return failure(GamesGetError.GenreNotFound)
        val games = memoryDB.gameDB.getGames(gen, developer)
        memoryDB.gameDB.getDeveloperByName(developer) ?: return failure(GamesGetError.DeveloperNotFound)
        success(games)
    }

    fun createGame(name: String, developer: String, genres: List<String>): GameCreationResult = run {
        val gen = genres.mapNotNull { it.toGenre() }
        if (gen.size < genres.size)
            return failure(GameCreationError.InvalidGenre)
        val gid = memoryDB.gameDB.createGame(name, developer, gen)
        if (gid != null) success(gid)
        else failure(GameCreationError.NameAlreadyExists)
    }
}