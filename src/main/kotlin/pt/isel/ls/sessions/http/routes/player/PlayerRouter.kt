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
import pt.isel.ls.sessions.http.model.player.PlayerCreateDTO
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.utils.TokenDTO
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.routes.utils.bearerTokenOrThrow
import pt.isel.ls.sessions.http.util.Problem
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.http.util.execStart
import pt.isel.ls.sessions.http.util.getPathSegments
import pt.isel.ls.sessions.http.util.jsonResponse
import pt.isel.ls.sessions.services.player.PlayerCreationError
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class PlayerRouter(private val services: PlayerService) : Router {
    override val routes =
        routes(
            Uris.DEFAULT bind POST to ::createPlayer,
            Uris.Players.SEARCH bind GET to ::searchPlayers,
            Uris.Players.BY_ID bind GET to ::getDetailsPlayer,
        )

    private fun createPlayer(request: Request): Response =
        execStart(request) {
            val player = Json.decodeFromString<PlayerCreateDTO>(request.bodyString())
            return when (val res = services.createPlayer(player.name, player.email)) {
                is Failure ->
                    when (res.value) {
                        PlayerCreationError.EmailExists -> Problem.emailAlreadyExists(request.uri, player.email)
                        PlayerCreationError.InvalidEmail -> Problem.invalidEmail(request.uri)
                        PlayerCreationError.NameExists -> Problem.nameAlreadyExists(request.uri, player.name)
                    }

                is Success ->
                    Response(Status.CREATED).header("Location", "/player/${res.value.pid}")
                        .jsonResponse(TokenDTO(res.value.pid, res.value.token.toString()))
            }
        }

    private fun getDetailsPlayer(request: Request): Response =
        execStart(request) {
            request.bearerTokenOrThrow() // Throws if no token is present
            val numberPlayer = request.getPathSegments(PLAYER_ID).first().toUInt()
            return when (val player = services.getDetailsPlayer(numberPlayer)) {
                is Failure -> Problem.playerNotFound(request.uri, numberPlayer)
                is Success ->
                    Response(Status.OK).jsonResponse(
                        PlayerDTO(
                            player.value.pid,
                            player.value.name,
                            player.value.email.value,
                        ),
                    )
            }
        }

    private fun searchPlayers(request: Request): Response =
        execStart(request) {
            val limit = request.query(LIMIT)?.toInt() ?: DEFAULT_LIMIT
            val skip = request.query(SKIP)?.toInt() ?: DEFAULT_SKIP
            val name = request.query("name") ?: ""
            if (limit < 0 || skip < 0) {
                return@execStart Problem.invalidSkipOrLimit(request.uri)
            }
            val players = services.searchPlayers(name, limit, skip)
            return Response(Status.OK).jsonResponse(
                players.map {
                    PlayerDTO(it.pid, it.name, it.email.value)
                },
            )
        }

    companion object {
        private val logger = LoggerFactory.getLogger(PlayerRouter::class.java)
        private const val PLAYER_ID = "pid"
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10
        private const val LIMIT = "limit"
        private const val SKIP = "skip"

        fun routes(services: PlayerService) = PlayerRouter(services).routes
    }
}
