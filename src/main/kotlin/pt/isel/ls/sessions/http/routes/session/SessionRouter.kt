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
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.services.session.SessionService

//TODO: USE INPUT AND OUTPUT MODELS
//TODO: START DEALING WITH EITHERS AND PROBLEMS
class SessionRouter(
    private val services: SessionService
) : Router {

    override val routes: RoutingHttpHandler = routes(
        Uris.Sessions.ROOT bind GET to ::getSessions,
        Uris.Sessions.GET_BY_ID bind GET to ::getSession,
        Uris.Sessions.CREATE bind POST to ::createSession,
        Uris.Sessions.ADD_PLAYER bind PUT to ::addPlayerToSession
    )

    //TODO: SARAIVA
    private fun getSessions(request: Request): Response {
        TODO()
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