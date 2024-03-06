package pt.isel.ls.sessions


import kotlinx.datetime.Clock
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.AppWebApi
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.AppService

private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.AppServer")

const val DEFAULT_PORT = 1904

class AppServer(private val port: Int, private val database: AppMemoryDB) {
    private val service = AppService(database)
    val webApi = AppWebApi(service)
}

fun getDate(request: Request): Response {
    return Response(OK)
        .header("content-type", "text/plain")
        .body(Clock.System.now().toString())
}

fun logRequest(request: Request) {
    logger.info(
        "incoming request: method={}, uri={}, content-type={} accept={}",
        request.method,
        request.uri,
        request.header("content-type"),
        request.header("accept"),
    )
}

fun main() {
    val database = AppMemoryDB()
    val server = AppServer(DEFAULT_PORT, database)
    val app = routes(
        server.webApi.routes,
        "date" bind GET to ::getDate,
    )

    val jettyServer = app.asServer(Jetty(DEFAULT_PORT)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    logger.info("leaving Main")
}