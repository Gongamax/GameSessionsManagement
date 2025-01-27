package pt.isel.ls.sessions.services.game

import pt.isel.ls.sessions.domain.game.Game
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
    data object GenreNotFound : GamesGetError()

    data object DeveloperNotFound : GamesGetError()
}

typealias GamesGetResult = Either<GamesGetError, List<Game>>

sealed class GameSearchError {
    data object NoGamesFound : GameSearchError()
}

typealias GamesSearchResult = Either<GameSearchError, List<Game>>
