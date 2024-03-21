package pt.isel.ls.sessions.http

import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sessions.http.routes.game.GameRouter
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.http.routes.session.SessionRouter
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.services.AppService

/**
 * Class representing the web API for the application.
 *
 * @param service The application service that provides the business logic for the application.
 * @property routes The routes for the web API. These are defined using the http4k routing DSL.
 * @property httpHandler The http handler for the web API. This is used to handle incoming HTTP requests.
 */
class AppWebApi(service: AppService) {
    /**
     * The routes for the web API. These are defined using the http4k routing DSL.
     * The routes are bound to the respective services for players, games, and sessions.
     */
    private val routes: RoutingHttpHandler =
        routes(
            Uris.Players.ROOT bind PlayerRouter.routes(service.playerService),
            Uris.Games.ROOT bind GameRouter.routes(service.gameService),
            Uris.Sessions.ROOT bind SessionRouter.routes(service.sessionService),
        )

    /**
     * The http handler for the web API.
     * This is used to handle incoming HTTP requests.
     * It is defined as the routes for the web API.
     */
    val httpHandler: RoutingHttpHandler = routes
}
