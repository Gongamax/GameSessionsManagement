package pt.isel.ls.sessions.http.routes.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.http4k.routing.RoutingHttpHandler
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.util.Uris
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
import pt.isel.ls.sessions.http.util.execStart
import pt.isel.ls.sessions.http.util.json
import pt.isel.ls.sessions.services.session.SessionAddPlayerError
import pt.isel.ls.sessions.services.session.SessionCreationError
import pt.isel.ls.sessions.services.session.SessionService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class SessionRouter(
    private val services: SessionService
) : Router {

    override val routes: RoutingHttpHandler = routes(
        Uris.DEFAULT bind GET to ::getSessions,
        Uris.Sessions.GET_BY_ID bind GET to ::getSession,
        Uris.DEFAULT bind POST to ::createSession,
        Uris.Sessions.ADD_PLAYER bind PUT to ::addPlayerToSession
    )

    //TODO: SARAIVA
    private fun getSessions(request: Request): Response = execStart(request) {
        return when (val gid = request.query("gid")?.toInt()) {
            null -> Response(Status.BAD_REQUEST).body("Invalid game id")
            else -> {
                val pid = request.query("pid")?.toUInt()
                val date = request.query("date")?.let { LocalDateTime.parse(it) }
                val state = request.query("state")?.let { SessionState.valueOf(it) }
                return when (val res = services.getSessions(gid, date, state, pid)) {
                    is Failure -> Response(Status.NOT_FOUND).json(MessageResponse("Sessions not found"))

                    is Success -> Response(Status.OK)
                        .json(res.value.map {
                            SessionsDTO(
                                it.sid, it.associatedPlayers.size, it.date,
                                it.gid, it.associatedPlayers.map { p -> PlayerDTO(p.name, p.email) },
                                it.capacity
                            )
                        })
                }
            }
        }
    }

    private fun getSession(request: Request): Response = execStart(request) {
        return when (val sid = request.path("sid")?.toInt()) {
            null -> Response(Status.BAD_REQUEST).body("Invalid session id")
            else -> when (val res = services.getSession(sid)) {
                is Failure -> Response(Status.NOT_FOUND).json(MessageResponse("Session not found"))

                is Success -> Response(Status.OK).json(
                    SessionDTO(
                        res.value.capacity, res.value.gid,
                        res.value.date.toString()
                    )
                )
            }
        }
    }

    private fun createSession(request: Request): Response = execStart(request) {
        val sessionDTO = Json.decodeFromString<SessionDTO>(request.bodyString())
        val date = LocalDateTime.parse(sessionDTO.date)
        return when (val res = services.createSession(sessionDTO.capacity, sessionDTO.gid, date)) {
            is Failure -> when (res.value) {
                SessionCreationError.GameNotFound ->
                    Response(Status.NOT_FOUND).json(MessageResponse("Game not found"))

                SessionCreationError.InvalidDate ->
                    Response(Status.BAD_REQUEST).json(MessageResponse("Invalid date"))

                SessionCreationError.InvalidCapacity ->
                    Response(Status.BAD_REQUEST).json(MessageResponse("Invalid capacity"))
            }

            is Success ->
                Response(Status.CREATED)
                    .header("Location", "/sessions/${res.value}")
                    .json(MessageResponse("Session created: ${res.value}"))
        }
    }

    private fun addPlayerToSession(request: Request): Response = execStart(request) {
        val sid = request.path("sid")?.toInt()
        val pid = request.path("pid")?.toUInt()
        return when {
            sid == null || pid == null -> Response(Status.BAD_REQUEST)
                .json(MessageResponse("Invalid session or player id"))

            else -> when (val res = services.addPlayerToSession(sid, pid)) {
                is Failure -> when (res.value) {
                    SessionAddPlayerError.SessionNotFound ->
                        Response(Status.NOT_FOUND).json(MessageResponse("Session not found"))

                    SessionAddPlayerError.SessionFull ->
                        Response(Status.BAD_REQUEST).json(MessageResponse("Session is full"))

                    SessionAddPlayerError.PlayerNotFound ->
                        Response(Status.NOT_FOUND).json(MessageResponse("Player not found"))

                    SessionAddPlayerError.PlayerAlreadyInSession ->
                        Response(Status.BAD_REQUEST).json(MessageResponse("Player already in session"))
                }

                is Success -> Response(Status.OK).json(MessageResponse("Player added to session"))
            }
        }
    }

    companion object {
        fun routes(services: SessionService) = SessionRouter(services).routes
    }
}