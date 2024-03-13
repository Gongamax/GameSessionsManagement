package pt.isel.ls.sessions.repository.data.game

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.domain.game.Genres

interface GameDB {
    fun createGame(name: String, developer: String, genres: List<Genres>): Int?
    fun getGames(genres: List<Genres>, developer: String): List<Game>
    fun getGameById(gid: Int): Game?

    fun getDeveloperByName(developer: String): String?

    fun reset()
}