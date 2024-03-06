package pt.isel.ls.sessions.http


import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sessions.http.routes.game.GameRouter
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.services.AppService

class AppWebApi(service: AppService) {


    val routes: RoutingHttpHandler = routes(
        "/player" bind PlayerRouter.routes(service.playerService),
        "/game" bind GameRouter.routes(service.gameService)
    )

}