package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.toGenre
import pt.isel.ls.sessions.repository.GameRepository
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

class GameService(private val gameDB: GameRepository) {
    fun getGame(gid: UInt): GameGetByIdResult =
        run {
            val game = gameDB.getGameById(gid)
            if (game != null) {
                success(game)
            } else {
                failure(GameGetError.GameNotFound)
            }
        }

    fun getGames(
        genres: List<String>,
        developer: String,
        limit: Int,
        skip: Int,
    ): GamesGetResult =
        run {
            val gen = genres.mapNotNull { it.toGenre() }
            if (gen.size != genres.size) {
                return failure(GamesGetError.GenreNotFound)
            }
            if (developer.isNotBlank()) {
                gameDB.getDeveloperByName(developer) ?: return failure(GamesGetError.DeveloperNotFound)
            }
            val games = gameDB.getGames(gen, developer, limit, skip)
            success(games)
        }

    fun createGame(
        name: String,
        developer: String,
        genres: List<String>,
    ): GameCreationResult =
        run {
            val gen = genres.mapNotNull { it.toGenre() }
            if (gen.size != genres.size) {
                return failure(GameCreationError.InvalidGenre)
            }
            if (gameDB.getGameByName(name)) {
                return failure(GameCreationError.NameAlreadyExists)
            }
            val gid = gameDB.createGame(name, developer, gen)
            success(gid)
        }
}
