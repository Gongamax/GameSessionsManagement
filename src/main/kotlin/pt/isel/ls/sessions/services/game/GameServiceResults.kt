package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.utils.Either

sealed class GameCreationError {
    data object NameAlreadyExists : GameCreationError() {
        override fun toString(): String = "The game name already exists."
    }

    data object InvalidGenre : GameCreationError() {
        override fun toString(): String = "The genre is invalid."
    }
}

typealias GameCreationResult = Either<GameCreationError, Int>

sealed class GameGetError {
    data object GameNotFound : GameGetError()
}

typealias GameGetByIdResult = Either<GameGetError, Game>


sealed class GamesGetError {

    data object NoGamesFound : GamesGetError()

    data object GenreNotFound : GamesGetError()

    data object DeveloperNotFound : GamesGetError()

}

typealias GamesGetResult = Either<GamesGetError, List<Game>>

