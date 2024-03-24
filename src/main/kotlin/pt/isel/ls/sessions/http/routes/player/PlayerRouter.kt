package pt.isel.ls.sessions.http.routes.player

import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.routes.utils.bearerTokenOrThrow
import pt.isel.ls.sessions.http.util.*
import pt.isel.ls.sessions.services.player.PlayerCreationError
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class PlayerRouter(private val services: PlayerService) : Router {
    override val routes =
        routes(
            Uris.DEFAULT bind POST to ::createPlayer,
            Uris.Players.BY_ID bind GET to ::getDetailsPlayer,
        )

    private fun createPlayer(request: Request): Response =
        execStart(request) {
            val player = Json.decodeFromString<PlayerDTO>(request.bodyString())
            return when (val res = services.createPlayer(player.name, player.email)) {
                is Failure ->
                    when (res.value) {
                        PlayerCreationError.EmailExists -> Problem.emailAlreadyExists(request.uri, player.email)
                        PlayerCreationError.InvalidEmail -> Problem.invalidEmail(request.uri)
                    }

                is Success ->
                    Response(Status.CREATED).header("Location", "/player/${res.value.pid}")
                        .jsonResponse(MessageResponse("Player created: ${res.value.pid}"))
            }
        }

    private fun getDetailsPlayer(request: Request): Response =
        execStart(request) {
            val token = request.bearerTokenOrThrow()
            val numberPlayer = request.getPathSegments(PLAYER_ID).first().toUInt()
            return when (val player = services.getDetailsPlayer(numberPlayer)) {
                is Failure -> Problem.playerNotFound(request.uri, numberPlayer)
                is Success -> Response(Status.OK).jsonResponse(PlayerDTO(player.value.name, player.value.email.value))
            }
        }

    companion object {
        private val logger = LoggerFactory.getLogger(PlayerRouter::class.java)
        private const val PLAYER_ID = "pid"

        fun routes(services: PlayerService) = PlayerRouter(services).routes
    }
}
