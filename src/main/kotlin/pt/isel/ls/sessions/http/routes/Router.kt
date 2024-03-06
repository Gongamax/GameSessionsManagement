package pt.isel.ls.sessions.http.routes

import org.http4k.routing.RoutingHttpHandler

interface Router {
    val routes: RoutingHttpHandler
}