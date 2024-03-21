package pt.isel.ls.sessions.domain.player

/**
 * Data class representing a Player email.
 *
 * @property value The email value.
 */
@JvmInline
value class Email(val value : String) {
    init {
        require(isValidEmail(value)) { "The player email must be a valid email" }
    }

    companion object {
        /**
         * Checks if the given email is valid.
         *
         * @param email The email to be checked.
         * @return True if the email is valid, false otherwise.
         */
        fun isValidEmail(email: String): Boolean {
            return email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))
        }
    }
}
