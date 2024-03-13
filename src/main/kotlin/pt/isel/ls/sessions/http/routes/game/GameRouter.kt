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
import pt.isel.ls.sessions.http.model.game.GameInputModel
import pt.isel.ls.sessions.http.model.game.GameOutputModel
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.utils.Either
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class GameRouter(private val services: GameService) : Router {

    override val routes: RoutingHttpHandler = routes(
        Uris.DEFAULT bind Method.GET to ::getGames,
        Uris.DEFAULT bind Method.POST to ::createGame,
        Uris.Games.BY_ID bind Method.GET to ::getGame,
    )

    private fun getGame(request: Request): Response {
        logRequest(request)
        val gid = request.path("gid")?.toInt() ?: return Response(Status.BAD_REQUEST)
        return when (val game = services.getGame(gid)) {
            is Either.Left -> Response(Status.NOT_FOUND)
            is Either.Right -> Response(Status.OK)
                .body(
                    Json.encodeToString(
                        GameOutputModel(
                            game.value.name,
                            game.value.developer,
                            game.value.genres.map { it.name }
                        )
                    )
                ).header("Content-Type", "application/json")
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

        return when (val games = services.getGames(genres, developer, limit, skip)) {
            is Either.Left -> Response(Status.NOT_FOUND)
                .header("content-type", "application/json")
                .body(Json.encodeToString(games.value.toString()))

            is Either.Right -> {
                val gamesDTOs = games.value.map { game ->
                    GameOutputModel(game.name, game.developer, game.genres.map { it.name })
                }
                return Response(Status.OK)
                    .header("content-type", "application/json")
                    .body(Json.encodeToString(gamesDTOs))
            }
        }
    }

    private fun createGame(request: Request): Response {
        logRequest(request)
        val game = Json.decodeFromString<GameInputModel>(request.bodyString())
        if (game.name == null || game.developer == null || game.genres.isEmpty())
            return Response(Status.EXPECTATION_FAILED)
                .header("content-type", "application/json")
                .body("Invalid game data")
        logger.info(
            "Game created: name:{}, email:{}, genres:{}", game.name,
            game.developer, game.genres
        )
        return when (val gameId = services.createGame(game.name, game.developer, game.genres)) {
            is Failure -> Response(Status.BAD_REQUEST)
                .header("content-type", "application/json")
                .body(Json.encodeToString(gameId.value.toString()))

            is Success -> Response(Status.CREATED)
                .header("Location", "/games/${gameId.value}")
                .body("Game id: ${gameId.value}")
        }
    }

    companion object {
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10
        fun routes(services: GameService) = GameRouter(services).routes
        private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.routes.game.GameRouter")

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