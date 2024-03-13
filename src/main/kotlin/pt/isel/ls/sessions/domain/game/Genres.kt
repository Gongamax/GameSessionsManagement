package pt.isel.ls.sessions.domain.game

enum class Genres {
    RPG,
    ADVENTURE,
    SHOOTER,
    TURN_BASED,
    ACTION,
    MULTIPLAYER,
    FIGHTING,
    SPORTS,
}

fun String.toGenre():Genres? = run {
    try {
        Genres.valueOf(this.uppercase())
    }catch (e:IllegalArgumentException){
        null
    }
}