package pt.isel.ls.sessions.domain.player

/**
 * Data class representing a Player email.
 *
 * @property value The email value.
 */
data class Email(val value : String) {
    init {
        require(isValidEmail(value)) { "The player email must be a valid email" }
    }

    companion object {
        fun isValidEmail(email: String): Boolean {
            return email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))
        }
    }
}
