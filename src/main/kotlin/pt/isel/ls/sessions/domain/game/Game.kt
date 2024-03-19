package pt.isel.ls.sessions.domain.game

/**
 * Data class representing a Game.
 *
 * @property gid The unique identifier of the game.
 * @property name The name of the game.
 * @property developer The developer of the game.
 * @property genres The genre of the game, represented as an enum.
 */
data class Game(val gid: UInt, val name: String, val developer: String, val genres: List<Genres>){
    init {
        require(gid > 0u) { "The game id must be a positive number" }
        require(name.isNotBlank()) { "The game name must not be blank" }
        require(developer.isNotBlank()) { "The game developer must not be blank" }
        require(genres.isNotEmpty()) { "The game must have at least one genre" }
    }
}
