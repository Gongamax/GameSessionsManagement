package pt.isel.ls.sessions.domain.game

/**
 * Data class representing a Game.
 *
 * @property gid The unique identifier of the game.
 * @property name The name of the game.
 * @property developer The developer of the game.
 * @property genres The genre of the game, represented as an enum.
 */

data class Game(val gid: Int, val name : String, val developer: String, val genres: Genres){
    enum class Genres {
        RPG, ADVENTURE, SHOOTER, TURN_BASED, ACTION;


        override fun toString():String{
            return when(this){
                RPG -> "RPG"
                ADVENTURE -> "ADVENTURE"
                SHOOTER -> "SHOOTER"
                TURN_BASED -> "TURN_BASED"
                ACTION -> "ACTION"
            }
        }
    }


}

