package pt.isel.ls.sessions.http.routes.player


import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.EXPECTATION_FAILED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.utils.TokenDTO
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.services.player.PlayerService

private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.routes.player.PlayerRouter")


class PlayerRouter(private val services: PlayerService) : Router {

    companion object {
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10

        fun routes(services: PlayerService) = PlayerRouter(services).routes

    }

    private fun logRequest(request: Request) {
        logger.info(
            "incoming request: method={}, uri={}, content-type={} accept={}",
            request.method,
            request.uri,
            request.header("content-type"),
            request.header("accept"),
        )
    }

    override val routes = routes(
       Uris.DEFAULT bind POST to ::createPlayer,
       Uris.Players.BY_ID bind GET to ::getDetailsPlayer
    )


    private fun createPlayer(request: Request): Response {
        logRequest(request)
        val player = Json.decodeFromString<PlayerDTO>(request.bodyString())
        try {
            val response = services.createPlayer(player.name, player.email)
            logger.info("Player created: name={}, email={}", player.name, player.email)

            return Response(CREATED)
                .header("content-type", "application/json")
                .body(Json.encodeToString(TokenDTO(response.pid, response.token)))
        } catch (error: Exception) {
            return Response(EXPECTATION_FAILED)
                .header("content-type", "application/json")
                .body(Json.encodeToString(error.message))
        }

    }

    private fun getDetailsPlayer(request: Request): Response {
        val numberPlayer = request.path("pid")?.toInt()
        val player = numberPlayer?.let { services.getDetailsPlayer(it) }
        if (player == null) return Response(OK).header("content-type", "application/json").body("Player not found")
        logRequest(request)
        return Response(OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(PlayerDTO(player.name, player.email)))
    }


}