package pt.isel.ls.sessions.http


import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sessions.http.routes.game.GameRouter
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.http.routes.session.SessionRouter
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.services.AppService

class AppWebApi(service: AppService) {
    val routes: RoutingHttpHandler = routes(
        Uris.Players.ROOT bind PlayerRouter.routes(service.playerService),
        Uris.Games.ROOT bind GameRouter.routes(service.gameService),
        Uris.Sessions.ROOT bind SessionRouter.routes(service.sessionService)
    )
}