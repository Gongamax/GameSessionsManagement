package pt.isel.ls.sessions.services.player

import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.utils.Token
import pt.isel.ls.utils.Either

sealed class PlayerCreationError {
    data object EmailExists : PlayerCreationError()

    data object InvalidEmail : PlayerCreationError()

    data object NameExists : PlayerCreationError()
}

typealias PlayerCreationResult = Either<PlayerCreationError, Token>

sealed class PlayerGetError {
    data object PlayerNotFound : PlayerGetError()
}

typealias PlayerGetResult = Either<PlayerGetError, Player>

typealias PlayerSearchResult = List<Player>
