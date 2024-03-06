package pt.isel.ls.sessions.domain.player

/**
 * Data class representing a Player.
 *
 * @property pid The unique identifier of the player.
 * @property name The name of the player.
 * @property email The unique email of the player.
 */
data class Player(val pid: Int, val name: String, val email: String) {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))
        }
    }
}