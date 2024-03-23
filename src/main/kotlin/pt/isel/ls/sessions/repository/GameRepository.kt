package pt.isel.ls.sessions.repository

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.domain.game.Genres

interface GameRepository {
    fun createGame(
        name: String,
        developer: String,
        genres: List<Genres>,
    ): UInt

    fun getGames(
        genres: List<Genres>,
        developer: String,
        limit: Int,
        skip: Int,
    ): List<Game>

    fun getGameById(gid: UInt): Game?

    fun getDeveloperByName(developer: String): String?

    fun getGameByName(name: String): Boolean

    fun reset()
}
