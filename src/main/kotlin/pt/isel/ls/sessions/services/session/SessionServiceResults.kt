package pt.isel.ls.sessions.services.session

import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.utils.Either

sealed class SessionCreationError {
    data object GameNotFound : SessionCreationError()
    data object InvalidDate : SessionCreationError()
    data object InvalidCapacity : SessionCreationError()
}

typealias SessionCreationResult = Either<SessionCreationError, UInt>

sealed class SessionsGetError {
    data object GameNotFound : SessionsGetError()

    data object SessionNotFound : SessionsGetError()

}
typealias SessionsGetResult = Either<SessionsGetError, List<Session>>

sealed class SessionGetError {
    data object SessionNotFound : SessionGetError()
}

typealias SessionGetResult = Either<SessionGetError, Session>

sealed class SessionAddPlayerError {
    data object SessionNotFound : SessionAddPlayerError()
    data object PlayerNotFound : SessionAddPlayerError()
    data object SessionFull : SessionAddPlayerError()
    data object PlayerAlreadyInSession : SessionAddPlayerError()
}

typealias SessionAddPlayerResult = Either<SessionAddPlayerError, Unit>