package pt.isel.ls.sessions.http.routes.player


import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.http.util.execStart
import pt.isel.ls.sessions.http.util.json
import pt.isel.ls.sessions.services.player.PlayerCreationError
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.routes.player.PlayerRouter")


class PlayerRouter(private val services: PlayerService) : Router {

    override val routes = routes(
        Uris.DEFAULT bind POST to ::createPlayer,
        Uris.Players.BY_ID bind GET to ::getDetailsPlayer
    )
    
    private fun createPlayer(request: Request): Response = execStart(request) {
        val player = Json.decodeFromString<PlayerDTO>(request.bodyString())
        return when (val res = services.createPlayer(player.name, player.email)) {
            is Failure -> Response(Status.BAD_REQUEST).json(MessageResponse("Email already exists"))

            is Success -> Response(Status.CREATED)
                .header("Location", "/player/${res.value.pid}")
                .json(MessageResponse("Player create: ${res.value.pid}"))

        }
    }

    private fun getDetailsPlayer(request: Request): Response = execStart(request) {
        val numberPlayer = request.path("pid")?.toUInt()
        return when {
            numberPlayer == null -> Response(Status.BAD_REQUEST)
                .json(MessageResponse("Invalid pid "))

            else -> when (val player = services.getDetailsPlayer(numberPlayer)) {
                is Failure -> Response(Status.NOT_FOUND).json(MessageResponse("Player not found"))
                is Success -> Response(Status.OK).json(
                    PlayerDTO(player.value.name, player.value.email)
                )

            }
        }
    }

    companion object {
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10

        fun routes(services: PlayerService) = PlayerRouter(services).routes

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