package pt.isel.ls.sessions.domain.player

/**
 * Data class representing a Player.
 *
 * @property pid The unique identifier of the player.
 * @property name The name of the player.
 * @property email The unique email of the player.
 */
data class Player(val pid: UInt, val name: String, val email: String) {
    init {
        require(pid > 0u) { "The player id must be a positive number" }
        require(name.isNotBlank()) { "The player name must not be blank" }
        require(isValidEmail(email)) { "The player email must be a valid email" }
    }

    companion object {
        fun isValidEmail(email: String): Boolean {
            return email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))
        }
    }
}