package pt.isel.ls.sessions.http.routes.game

import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.http.model.game.GameDTO
import pt.isel.ls.sessions.http.model.game.GameInputDTO
import pt.isel.ls.sessions.http.model.game.GamesDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.http.routes.utils.bearerTokenOrThrow
import pt.isel.ls.sessions.http.util.LOCATION
import pt.isel.ls.sessions.http.util.Problem
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.http.util.execStart
import pt.isel.ls.sessions.http.util.getPathSegments
import pt.isel.ls.sessions.http.util.jsonResponse
import pt.isel.ls.sessions.services.game.GameCreationError
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.game.GamesGetError
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success

class GameRouter(private val services: GameService) : Router {
    override val routes: RoutingHttpHandler =
        routes(
            Uris.DEFAULT bind Method.GET to ::getGames,
            Uris.DEFAULT bind Method.POST to ::createGame,
            Uris.Games.BY_ID bind Method.GET to ::getGame,
        )

    private fun createGame(request: Request): Response =
        execStart(request) {
            val game = Json.decodeFromString<GameInputDTO>(request.bodyString())
            request.bearerTokenOrThrow()
            if (game.name.isBlank() || game.developer.isBlank() || game.genres.isEmpty()) {
                return@execStart Problem.invalidGameData(request.uri)
            }
            return handleCreateGame(request, game)
        }

    private fun getGame(request: Request): Response =
        execStart(request) {
            val gid = request.getPathSegments(GAME_ID).first().toUInt()
            return handleGetGame(request, gid)
        }

    private fun getGames(request: Request): Response =
        execStart(request) {
            val limit = request.query(LIMIT)?.toInt() ?: DEFAULT_LIMIT
            val skip = request.query(SKIP)?.toInt() ?: DEFAULT_SKIP
            if (limit < 0 || skip < 0) {
                return@execStart Problem.invalidSkipOrLimit(request.uri)
            }
            val name = request.query("name") ?: ""
            if (name.isNotEmpty()) {
                return handleSearchByName(request, name, limit, skip)
            }
            val developer = request.query("developer") ?: ""
            val genres = getGenres(request)

            return handleGetGames(request, genres, developer, limit, skip)
        }

    private fun getGenres(request: Request): List<String> {
        return request.query("genres")?.trim()?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    // Auxiliary functions
    private fun handleGetGame(
        request: Request,
        gid: UInt,
    ): Response {
        return when (val game = services.getGame(gid)) {
            is Failure -> Problem.gameNotFound(request.uri)
            is Success -> Response(Status.OK).jsonResponse(game.value.toGameDTO())
        }
    }

    private fun handleSearchByName(
        request: Request,
        name: String,
        limit: Int,
        skip: Int,
    ): Response {
        return when (val games = services.searchGamesByName(name, limit, skip)) {
            is Failure -> Problem.gameNotFound(request.uri)
            is Success -> Response(Status.OK).jsonResponse(GamesDTO(games.value.map { g -> g.toGameDTO() }))
        }
    }

    private fun handleGetGames(
        request: Request,
        genres: List<String>,
        developer: String,
        limit: Int,
        skip: Int,
    ): Response {
        return when (val games = services.getGames(genres, developer, limit, skip)) {
            is Failure ->
                when (games.value) {
                    GamesGetError.GenreNotFound -> Problem.genreNotFound(request.uri)
                    GamesGetError.DeveloperNotFound -> Problem.developerNotFound(request.uri)
                }

            is Success -> Response(Status.OK).jsonResponse(GamesDTO(games.value.map { g -> g.toGameDTO() }))
        }
    }

    private fun handleCreateGame(
        request: Request,
        game: GameInputDTO,
    ): Response {
        return when (val gameId = services.createGame(game.name, game.developer, game.genres)) {
            is Failure ->
                when (gameId.value) {
                    GameCreationError.InvalidGenre -> Problem.genreNotFound(request.uri)
                    GameCreationError.NameAlreadyExists -> Problem.nameAlreadyExists(request.uri, game.name)
                }

            is Success ->
                Response(Status.CREATED).header(LOCATION, "/game/${gameId.value}").jsonResponse(
                    MessageResponse("Game created: ${gameId.value}"),
                )
        }
    }

    companion object {
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_LIMIT = 10
        private const val GAME_ID = "gid"
        private const val LIMIT = "limit"
        private const val SKIP = "skip"

        fun routes(services: GameService) = GameRouter(services).routes

        private fun Game.toGameDTO() = GameDTO(gid, name, developer, genres.map { g -> g.text })
    }
}
