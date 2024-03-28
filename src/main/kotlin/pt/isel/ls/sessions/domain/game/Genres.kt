package pt.isel.ls.sessions.domain.game

/**
 * Enum class representing the different genres of games.
 *
 * @property text The name of the genre.
 */
enum class Genres(val text: String) {
    RPG("Rpg"),
    ADVENTURE("Adventure"),
    SHOOTER("Shooter"),
    TURN_BASED("Turn-Based"),
    ACTION("Action"),
    MULTIPLAYER("Multiplayer"),
    FIGHTING(
        "Fighting",
    ),
    SPORTS("Sports"),
    ;

    /**
     * Initialization block to ensure that the genre text is not blank.
     */
    init {
        require(text.isNotBlank()) { "The genre text must not be blank" }
    }
}

/**
 * Extension function to convert a string to a Genres enum.
 *
 * @receiver The string to be converted.
 * @return The corresponding Genres enum if it exists, null otherwise.
 */
fun String.toGenre(): Genres? {
    val upper = this.uppercase()
    return Genres.entries.firstOrNull { it.text.uppercase() == upper }
}
