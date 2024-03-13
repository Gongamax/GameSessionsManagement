package pt.isel.ls.sessions.http.routes.game

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.domain.game.toGenre
import pt.isel.ls.sessions.http.model.game.GameDTO
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.services.game.GameGetByIdError
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.utils.Either

class GameRouter(private val services: GameService) : Router {


    private fun logRequest(request: Request) {
        logger.info(
            "incoming request: method={}, uri={}, content-type={} accept={}",
            request.method,
            request.uri,
            request.header("content-type"),
            request.header("accept"),
        )
    }

    override val routes: RoutingHttpHandler = routes(
        "" bind Method.GET to ::getGames,
        "" bind Method.POST to ::createGame,
        "/{gid}" bind Method.GET to ::getGame,
    )

    private fun getGame(request: Request): Response {
        logRequest(request)
        val gameId = request.path("gid")?.toInt() ?: return Response(Status.BAD_REQUEST)
            .header("content-type", "application/json")
            .body("Invalid game id")
        val game = services.getGame(gameId)
        return when (game) {
            is Either.Left -> Response(Status.NOT_FOUND)
                .header("content-type", "application/json")
                .body("Game not found $gameId")

            is Either.Right -> Response(Status.OK)
                .header("content-type", "application/json")
                .body(
                    Json.encodeToString(
                        GameDTO(
                            game.value.name,
                            game.value.developer,
                            game.value.genres.map { it.name })
                    )
                )
        }
    }

    private fun getGames(request: Request): Response {
        logRequest(request)
        val genres = request.query("genres")?.split(",")
        val developer = request.query("developer")
        val limit = request.query("limit")?.toInt() ?: DEFAULT_LIMIT
        val skip = request.query("skip")?.toInt() ?: DEFAULT_SKIP
        if (genres == null || developer == null)
            return Response(Status.EXPECTATION_FAILED)
                .header("content-type", "application/json")
                .body("No genres or developer specified")

        val games = services.getGames(genres, developer, limit, skip)
        return when (games) {
            is Either.Left -> Response(Status.NOT_FOUND)
                .header("content-type", "application/json")
                .body(Json.encodeToString(games.value.toString()))

            is Either.Right -> {
                val gamesDTOs = games.value.map { game ->
                    GameDTO(game.name, game.developer, game.genres.map { it.name })
                }
                return Response(Status.OK)
                    .header("content-type", "application/json")
                    .body(Json.encodeToString(gamesDTOs))
            }
        }


    }

    private fun createGame(request: Request): Response {
        logRequest(request)
        val game = Json.decodeFromString<GameDTO>(request.bodyString())
        if (game.name == null || game.developer == null || game.genres.isEmpty())
            return Response(Status.EXPECTATION_FAILED)
                .header("content-type", "application/json")
                .body("Invalid game data")
        val genres = game.genres
        val gameId = services.createGame(game.name, game.developer, genres)
        logger.info(
            "Game created: name:{}, email:{}, genres:{}", game.name,
            game.developer, game.genres
        )
        return when (gameId) {
            is Either.Left -> Response(Status.BAD_REQUEST)
                .header("content-type", "application/json")
                .body(Json.encodeToString(gameId.value.toString()))

            is Either.Right -> Response(Status.CREATED)
                .header("Location", "/games/${gameId.value}")
                .body("Game id: ${gameId.value}")
        }
    }

    companion object {
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10
        fun routes(services: GameService) = GameRouter(services).routes
        private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.routes.game.GameRouter")
    }
}