package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.utils.PageResult
import pt.isel.ls.utils.Either

sealed class GameCreationError {
    data object NameAlreadyExists : GameCreationError()

    data object InvalidGenre : GameCreationError()
}

typealias GameCreationResult = Either<GameCreationError, UInt>

sealed class GameGetError {
    data object GameNotFound : GameGetError()
}

typealias GameGetByIdResult = Either<GameGetError, Game>

sealed class GamesGetError {
    data object NoGamesFound : GamesGetError()

    data object GenreNotFound : GamesGetError()

    data object DeveloperNotFound : GamesGetError()
}

typealias GamesGetResult = Either<GamesGetError, PageResult<Game>>
