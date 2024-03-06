package pt.isel.ls.sessions.domain.game

enum class Genres {
    RPG,
    ADVENTURE,
    SHOOTER,
    TURN_BASED,
    ACTION
}

fun String.toGenre():Genres? = run {
    try {
        Genres.valueOf(this)
    }catch (e:IllegalArgumentException){
        null
    }
}