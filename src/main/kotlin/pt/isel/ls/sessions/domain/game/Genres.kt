package pt.isel.ls.sessions.domain.game

enum class Genres(val text: String) {
    RPG("Rpg"),
    ADVENTURE("Adventure"),
    SHOOTER("Shooter"),
    TURN_BASED("Turn-Based"),
    ACTION("Action"),
    MULTIPLAYER("Multiplayer"),
    FIGHTING("Fighting"),
    SPORTS("Sports"),
}

fun String.toGenre(): Genres? {
    val upper = this.uppercase()
    return Genres.entries.firstOrNull { it.text.uppercase() == upper }
}

