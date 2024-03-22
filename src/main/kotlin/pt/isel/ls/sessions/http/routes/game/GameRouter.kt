package pt.isel.ls.sessions.http.routes.game

import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.sessions.http.model.game.GameDTO
import pt.isel.ls.sessions.http.model.game.GamesInputModel
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.util.LOCATION
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.http.util.execStart
import pt.isel.ls.sessions.http.util.jsonResponse
import pt.isel.ls.sessions.services.game.GameCreationError
import pt.isel.ls.sessions.services.game.GameGetError
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.game.GamesGetError
import pt.isel.ls.utils.Either
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class GameRouter(private val services: GameService) : Router {
    override val routes: RoutingHttpHandler =
        routes(
            Uris.DEFAULT bind Method.GET to ::getGames,
            Uris.DEFAULT bind Method.POST to ::createGame,
            Uris.Games.BY_ID bind Method.GET to ::getGame,
        )

    private fun getGame(request: Request): Response =
        execStart(request) {
            val gid = request.path("gid")?.toUInt() ?: return Response(Status.BAD_REQUEST)
            return when (val game = services.getGame(gid)) {
                is Failure -> Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Game not found"))
                is Success ->
                    Response(Status.OK).jsonResponse(
                        GameDTO(game.value.name, game.value.developer, game.value.genres.map { g -> g.text }),
                    )
            }
        }

    private fun getGames(request: Request): Response =
        execStart(request) {
            val game = Json.decodeFromString<GamesInputModel>(request.bodyString())
            if (game.genres.isEmpty() || game.developer.isBlank())
                return Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Genres or developer is empty"))
            val limit = request.query("limit")?.toInt() ?: DEFAULT_LIMIT
            val skip = request.query("skip")?.toInt() ?: DEFAULT_SKIP
            if (limit < 0 || skip < 0)
                return Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Limit or skip is negative"))
            return when (val games = services.getGames(game.genres, game.developer, limit, skip)) {
                is Failure ->
                    when (games.value) {
                        GamesGetError.NoGamesFound -> Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Games not found"))

                        GamesGetError.GenreNotFound -> Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Genre not found"))

                        GamesGetError.DeveloperNotFound -> Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Developer not found"))
                    }

                is Success ->
                    Response(Status.OK).jsonResponse(
                        games.value.map {
                            GameDTO(it.name, game.developer, it.genres.map { g -> g.text })
                        },
                    )
            }
        }

    private fun createGame(request: Request): Response =
        execStart(request) {
            val game = Json.decodeFromString<GameDTO>(request.bodyString())
            if (game.name.isBlank() || game.developer.isBlank() || game.genres.isEmpty()) {
                return Response(Status.EXPECTATION_FAILED).jsonResponse(
                    MessageResponse("Invalid game data."),
                )
            }

            return when (val gameId = services.createGame(game.name, game.developer, game.genres)) {
                is Failure ->
                    when (gameId.value) {
                        GameCreationError.InvalidGenre -> Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Invalid genre, please check the genre name"))
                        GameCreationError.NameAlreadyExists -> Response(Status.CONFLICT).jsonResponse(
                            MessageResponse(
                                "Game name already exists"
                            )
                        )

                        is GameCreationError.UnknownError -> Response(Status.INTERNAL_SERVER_ERROR).jsonResponse(
                            MessageResponse(
                                "Failed to create game"
                            )
                        )
                    }

                is Success ->
                    Response(Status.CREATED).header(LOCATION, "/game/${gameId.value}").jsonResponse(
                        MessageResponse("Game id: ${gameId.value}"),
                    )
            }
        }

    companion object {
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10

        fun routes(services: GameService) = GameRouter(services).routes
    }
}
