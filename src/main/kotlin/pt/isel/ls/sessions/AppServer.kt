package pt.isel.ls.sessions

import kotlinx.datetime.Clock
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.AppWebApi
import pt.isel.ls.sessions.repository.AppDB
import pt.isel.ls.sessions.services.AppService

class AppServer(
    private val port: Int,
    private val database: AppDB,
    private val clock: Clock,
) {
    private val service = AppService(database, clock)
    private val webApi = AppWebApi(service)

    fun start() {
        val app = routes(
            API_PATH bind webApi.httpHandler,
            singlePageApp(ResourceLoader.Directory("static-content"))
        )
        try {
            val jettyServer = app.asServer(Jetty(port)).start()
            logger.info("server started listening on port $port")
            readln()
            jettyServer.stop()
            logger.info("server stopped")
        } catch (e: Exception) {
            logger.error("Error occurred: ${e.message}")
        }
    }

    // TODO: Think about having a stop function, probably useful for testing

    companion object {
        private val logger = LoggerFactory.getLogger(AppServer::class.java)
        private const val API_PATH = "api"
    }
}
