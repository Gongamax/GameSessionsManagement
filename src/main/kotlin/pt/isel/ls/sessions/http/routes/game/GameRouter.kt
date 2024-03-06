package pt.isel.ls.sessions.http.routes.game

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.routes.Router
import pt.isel.ls.sessions.services.game.GameService

class GameRouter(private val services: GameService): Router{
    private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.routes.game.GameRouter")

    companion object {
        fun routes(services: GameService) = GameRouter(services).routes
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
        "" bind Method.GET to ::getGames,
        "" bind Method.POST to ::createGame,
        "/{gid}" bind Method.GET to ::getGame
    )

    private fun getGame(request: Request): Response {
        TODO()
    }
    private fun getGames(request: Request): Response {
        TODO("Not yet implemented. Saraiva!!!")
    }
    private fun createGame(request: Request): Response {
        TODO()
    }
}