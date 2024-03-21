package pt.isel.ls.sessions.domain.player

/**
 * Data class representing a Player.
 *
 * @property pid The unique identifier of the player.
 * @property name The name of the player.
 * @property email The unique email of the player.
 */
data class Player(val pid: UInt, val name: String, val email: Email) {
    init {
        require(pid > 0u) { "The player id must be a positive number" }
        require(name.isNotBlank()) { "The player name must not be blank" }
    }
}
