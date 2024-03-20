package pt.isel.ls.sessions.http.routes.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.http4k.routing.RoutingHttpHandler
import pt.isel.ls.sessions.http.routes.Router
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.http.model.session.SessionsDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.util.*
import pt.isel.ls.sessions.http.util.getPathSegments
import pt.isel.ls.sessions.services.session.SessionAddPlayerError
import pt.isel.ls.sessions.services.session.SessionCreationError
import pt.isel.ls.sessions.services.session.SessionService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success
import pt.isel.ls.utils.handleEither

class SessionRouter(
    private val services: SessionService
) : Router {

    override val routes: RoutingHttpHandler = routes(
        Uris.DEFAULT bind GET to ::getSessions,
        Uris.Sessions.GET_BY_ID bind GET to ::getSession,
        Uris.DEFAULT bind POST to ::createSession,
        Uris.Sessions.ADD_PLAYER bind PUT to ::addPlayerToSession
    )

    private fun getSessions(request: Request): Response = execStart(request) {
        val pid = request.query(PLAYER_ID)?.toUInt()
        val gid = request.query(GAME_ID)?.toUInt() ?: return@execStart Response(Status.BAD_REQUEST).jsonResponse(
            MessageResponse("Game id not found")
        )
        val date = request.query(DATE)?.toLocalDateTime()
        val state = request.query(STATE)
        return when (val res = services.getSessions(gid, date, state?.toSessionState(), pid)) {
            is Failure -> Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Sessions not found"))

            is Success -> Response(Status.OK)
                .jsonResponse(res.value.map {
                    SessionsDTO(
                        it.sid, it.associatedPlayers.size, it.date,
                        it.gid, it.associatedPlayers.map { p -> PlayerDTO(p.name, p.email) },
                        it.capacity
                    )
                })
        }
    }

    private fun getSession(request: Request): Response = execStart(request) {
        val sid = request.getPathSegments(SESSION_ID).first().toUInt()
        return when (val res = services.getSession(sid)) {
            is Failure -> Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Session not found"))

            is Success -> Response(Status.OK).jsonResponse(
                SessionDTO(
                    res.value.capacity, res.value.gid,
                    res.value.date.toString()
                )
            )
        }
    }

    private fun createSession(request: Request): Response = execStart(request) {
        val sessionDTO = Json.decodeFromString<SessionDTO>(request.bodyString())
        val date = LocalDateTime.parse(sessionDTO.date)
        return when (val res = services.createSession(sessionDTO.capacity, sessionDTO.gid, date)) {
            is Failure -> when (res.value) {
                SessionCreationError.GameNotFound ->
                    Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Game not found"))

                SessionCreationError.InvalidDate ->
                    Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Invalid date"))

                SessionCreationError.InvalidCapacity ->
                    Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Invalid capacity"))
            }

            is Success ->
                Response(Status.CREATED)
                    .header("Location", "/sessions/${res.value}")
                    .jsonResponse(MessageResponse("Session created: ${res.value}"))
        }
    }

    private fun addPlayerToSession(request: Request): Response = execStart(request) {
        val (sid, pid) = request.getPathSegments(SESSION_ID, PLAYER_ID).map { it.toUInt() }
        return when (val res = services.addPlayerToSession(sid, pid)) {
            is Failure -> when (res.value) {
                SessionAddPlayerError.SessionNotFound ->
                    Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Session not found"))

                SessionAddPlayerError.SessionFull ->
                    Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Session is full"))

                SessionAddPlayerError.PlayerNotFound ->
                    Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Player not found"))

                SessionAddPlayerError.PlayerAlreadyInSession ->
                    Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Player already in session"))
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

        private fun String.toLocalDateTime() = LocalDateTime.parse(this)
        private fun String.toSessionState() = SessionState.valueOf(this)
    }
}