package pt.isel.ls.sessions.http.routes.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.session.SessionCreateDTO
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.routes.bearerTokenOrThrow
import pt.isel.ls.sessions.http.util.*
import pt.isel.ls.sessions.http.util.getPathSegments
import pt.isel.ls.sessions.services.session.SessionAddPlayerError
import pt.isel.ls.sessions.services.session.SessionCreationError
import pt.isel.ls.sessions.services.session.SessionService
import pt.isel.ls.sessions.services.session.SessionsGetError
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class SessionRouter(
    private val services: SessionService,
) : Router {
    override val routes: RoutingHttpHandler =
        routes(
            Uris.DEFAULT bind Method.GET to ::getSessions,
            Uris.Sessions.GET_BY_ID bind Method.GET to ::getSession,
            Uris.DEFAULT bind Method.POST to ::createSession,
            Uris.Sessions.ADD_PLAYER bind Method.PUT to ::addPlayerToSession,
        )

    private fun getSessions(request: Request): Response = execStart(request) {
        val pid = request.query(PLAYER_ID)?.toUInt()
        val gid = request.query(GAME_ID)?.toUInt() ?: return@execStart Response(Status.BAD_REQUEST).jsonResponse(
            MessageResponse("Game id not found")
        )
        val limit = request.query("limit")?.toInt() ?: DEFAULT_LIMIT
        val skip = request.query("skip")?.toInt() ?: DEFAULT_SKIP
        if (limit < 0 || skip < 0)
            return Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Limit or skip is negative"))
        val date = request.query(DATE)?.toLocalDateTime()
        val state = request.query(STATE)
        return when (val res = services.getSessions(gid, date, state?.toSessionState(), pid, limit, skip)) {
            is Failure ->
                when (res.value) {
                    SessionsGetError.GameNotFound -> Problem.gameNotFound(request.uri)
                    SessionsGetError.InvalidDate -> Problem.invalidDate(request.uri)
                    SessionsGetError.InvalidState -> Problem.invalidState(request.uri)
                    SessionsGetError.PlayerNotFound -> Problem.playerNotFound(request.uri, pid)
                }

            is Success ->
                Response(Status.OK).jsonResponse(
                    res.value.map {
                        SessionDTO(
                            it.sid,
                            it.associatedPlayers.size,
                            it.date,
                            it.gid,
                            it.associatedPlayers.map { p -> PlayerDTO(p.name, p.email.value) },
                            it.capacity,
                        )
                    },
                )
        }
    }

    private fun getSession(request: Request): Response =
        execStart(request) {
            val token = request.bearerTokenOrThrow()
            val sid = request.getPathSegments(SESSION_ID).first().toUInt()
            return when (val res = services.getSession(sid)) {
                is Failure -> Problem.sessionNotFound(request.uri, sid)

                is Success ->
                    Response(Status.OK).jsonResponse(
                        SessionDTO(
                            res.value.sid,
                            res.value.numberOfPlayers,
                            res.value.date,
                            res.value.gid,
                            res.value.associatedPlayers.map { p -> PlayerDTO(p.name, p.email.value) },
                            res.value.capacity,
                        ),
                    )
            }
        }

    private fun createSession(request: Request): Response =
        execStart(request) {
            val token = request.bearerTokenOrThrow()
            val sessionDTO = Json.decodeFromString<SessionCreateDTO>(request.bodyString())
            val date = LocalDateTime.parse(sessionDTO.date)
            return when (val res = services.createSession(sessionDTO.capacity, sessionDTO.gid, date)) {
                is Failure ->
                    when (res.value) {
                        SessionCreationError.GameNotFound -> Problem.gameNotFound(request.uri)
                        SessionCreationError.InvalidDate -> Problem.invalidDate(request.uri)
                        SessionCreationError.InvalidCapacity -> Problem.invalidCapacity(request.uri)
                    }

                is Success ->
                    Response(Status.CREATED).header("Location", "/sessions/${res.value}")
                        .jsonResponse(MessageResponse("Session created: ${res.value}"))
            }
        }

    private fun addPlayerToSession(request: Request): Response =
        execStart(request) {
            val token = request.bearerTokenOrThrow()
            val (sid, pid) = request.getPathSegments(SESSION_ID, PLAYER_ID).map { it.toUInt() }
            return when (val res = services.addPlayerToSession(sid, pid)) {
                is Failure ->
                    when (res.value) {
                        SessionAddPlayerError.SessionNotFound -> Problem.sessionNotFound(request.uri, sid)
                        SessionAddPlayerError.SessionFull -> Problem.sessionIsFull(request.uri)
                        SessionAddPlayerError.PlayerNotFound -> Problem.playerNotFound(request.uri, pid)
                        SessionAddPlayerError.PlayerAlreadyInSession -> Problem.playerAlreadyInSession(request.uri)
                    }

                is Success -> Response(Status.OK).jsonResponse(MessageResponse("Player added to session"))
            }
        }

    companion object {
        fun routes(services: SessionService) = SessionRouter(services).routes

        private const val SESSION_ID = "sid"
        private const val PLAYER_ID = "pid"
        private const val GAME_ID = "gid"
        private const val DATE = "date"
        private const val STATE = "state"

        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10

        private fun String.toLocalDateTime() = LocalDateTime.parse(this)

        private fun String.toSessionState() = SessionState.valueOf(this)
    }
}
