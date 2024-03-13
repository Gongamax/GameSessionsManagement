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
    data object NoGamesFound : GameGetError()
}

typealias GameGetResult = Either<GameGetError, Game>

sealed class GameGetByIdError {
    data object GameNotFound : GameGetByIdError()
}

typealias GameGetByIdResult = Either<GameGetByIdError, Game>


sealed class GamesGetError {
    data object GenreNotFound : GamesGetError() {
        override fun toString(): String = "The genre is invalid."
    }

    data object DeveloperNotFound : GamesGetError() {
        override fun toString(): String = "The developer was not found."
    }


}

typealias GamesGetResult = Either<GamesGetError, List<Game>>

