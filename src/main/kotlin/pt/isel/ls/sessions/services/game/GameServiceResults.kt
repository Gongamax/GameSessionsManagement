package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.utils.Either

sealed class GameCreationError {
    data object NameAlreadyExists : GameCreationError()
}

typealias GameCreationResult = Either<GameCreationError, Int>

sealed class GameGetError {
    data object NoGamesFound : GameGetError()
}

typealias GamesGetResult = Either<GameGetError, List<Game>>

sealed class GameGetByIdError {
    data object GameNotFound : GameGetByIdError()
}

typealias GameGetByIdResult = Either<GameGetByIdError, Game>