package pt.isel.ls.sessions.domain.game

/**
 * Data class representing a Game.
 *
 * @property gid The unique identifier of the game.
 * @property name The name of the game.
 * @property developer The developer of the game.
 * @property genres The genre of the game, represented as an enum.
 */

data class Game(val gid: Int, val name: String, val developer: String, val genres: List<Genres>)
