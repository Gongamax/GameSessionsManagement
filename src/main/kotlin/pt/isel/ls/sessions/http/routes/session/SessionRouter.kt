package pt.isel.ls.sessions.http.routes.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
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
import org.http4k.core.Uri
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.http.model.session.SessionsDTO
import pt.isel.ls.sessions.services.session.SessionService
import pt.isel.ls.utils.Either
import kotlin.math.log

//TODO: USE INPUT AND OUTPUT MODELS
//TODO: START DEALING WITH EITHERS AND PROBLEMS
class SessionRouter(
    private val services: SessionService
) : Router {

    override val routes: RoutingHttpHandler = routes(
        Uris.DEFAULT bind GET to ::getSessions,
        Uris.Sessions.GET_BY_ID bind GET to ::getSession,
        Uris.DEFAULT bind POST to ::createSession,
        Uris.Sessions.ADD_PLAYER bind PUT to ::addPlayerToSession
    )


    private fun getSessions(request: Request): Response {
        logRequest(request)
        val gid = request.query("gid")?.toInt()
        return when (gid) {
            null -> Response(Status.BAD_REQUEST).body("Invalid game id")
            else -> {
                val date = request.query("date")?.let { LocalDateTime.parse(it) }
                val state = request.query("state")?.let { SessionState.valueOf(it) }
                val pid = request.query("pid")?.toInt()
                val sessions = services.getSessions(gid, date, state, pid)
                return when (sessions) {
                    is Either.Left -> Response(Status.NOT_FOUND)
                        .header("content-type", "application/json")
                        .body("Sessions not found")

                    is Either.Right -> Response(Status.OK)
                        .header("content-type", "application/json")
                        .body(Json.encodeToString(sessions.value.map {
                            SessionsDTO(
                                it.sid, it.associatedPlayers.size, it.date,
                                it.gid, it.associatedPlayers.map { p -> PlayerDTO(p.name, p.email) },
                                it.capacity
                            )
                        }))
                }
            }
        }
    }

    private fun getSession(request: Request): Response {
        logRequest(request)
        val sid = request.path("sid")?.toInt()
        return when (sid) {
            null -> Response(Status.BAD_REQUEST).body("Invalid session id")
            else -> {
                val session = services.getSession(sid)
                Response(Status.OK)
                    .header("content-type", "application/json")
                    .body(Json.encodeToString(session))
            }
        }
    }

    private fun createSession(request: Request): Response {
        logRequest(request)
        val sessionDTO = Json.decodeFromString<SessionDTO>(request.bodyString())
        val date = LocalDateTime.parse(sessionDTO.date)
        val sessionId = services.createSession(sessionDTO.capacity, sessionDTO.gid, date)
        return Response(Status.CREATED)
            .header("Location", "/sessions/$sessionId")
            .body("Session id: $sessionId")
    }

    private fun addPlayerToSession(request: Request): Response {
        logRequest(request)
        val sid = request.path("sid")?.toInt()
        val pid = request.path("pid")?.toInt()
        return when {
            sid == null || pid == null -> Response(Status.BAD_REQUEST).body("Invalid session or player id")
            else -> {
                services.addPlayerToSession(sid, pid)
                Response(Status.NO_CONTENT)
            }
        }
    }

    companion object {
        fun routes(services: SessionService) = SessionRouter(services).routes
        private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.routes.session.SessionRouter")

        private fun logRequest(request: Request) {
            logger.info(
                "incoming request: method={}, uri={}, content-type={} accept={}",
                request.method,
                request.uri,
                request.header("content-type"),
                request.header("accept"),
            )
        }
    }
}