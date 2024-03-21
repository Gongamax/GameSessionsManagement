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
import pt.isel.ls.sessions.http.util.jsonResponse
import pt.isel.ls.sessions.services.player.PlayerCreationError
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class PlayerRouter(private val services: PlayerService) : Router {

    override val routes = routes(
        Uris.DEFAULT bind POST to ::createPlayer,
        Uris.Players.BY_ID bind GET to ::getDetailsPlayer
    )

    private fun createPlayer(request: Request): Response = execStart(request) {
        val player = Json.decodeFromString<PlayerDTO>(request.bodyString())
        return when (val res = services.createPlayer(player.name, player.email)) {
            is Failure -> when (res.value) {
                PlayerCreationError.EmailExists -> Response(Status.CONFLICT)
                    .jsonResponse(MessageResponse("Email already exists"))

                PlayerCreationError.InvalidEmail -> Response(Status.BAD_REQUEST)
                    .jsonResponse(MessageResponse("Invalid email"))
            }

            is Success -> Response(Status.CREATED)
                .header("Location", "/player/${res.value.pid}")
                .jsonResponse(MessageResponse("Player create: ${res.value.pid}"))

        }
    }

    private fun getDetailsPlayer(request: Request): Response = execStart(request) {
        val numberPlayer = request.path("pid")?.toUInt()
        return when {
            numberPlayer == null -> Response(Status.BAD_REQUEST)
                .jsonResponse(MessageResponse("Invalid pid "))

            else -> when (val player = services.getDetailsPlayer(numberPlayer)) {
                is Failure -> Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Player not found"))
                is Success -> Response(Status.OK).jsonResponse(
                    PlayerDTO(player.value.name, player.value.email.value)
                )

            }
        }
    }

    companion object {
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10
        private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.routes.player.PlayerRouter")

        fun routes(services: PlayerService) = PlayerRouter(services).routes
    }
}