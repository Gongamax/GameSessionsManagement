package pt.isel.ls.sessions

import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.AppWebApi
import pt.isel.ls.sessions.repository.AppDB
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.AppService

class AppServer(private val port: Int, private val database: AppDB) {
    private val service = AppService(database)
    private val webApi = AppWebApi(service)

    fun start() {
        val app = routes(
            API_PATH bind webApi.httpHandler,
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

    //TODO: Think about having a stop function, probably useful for testing

    companion object {
        private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.AppServer")
        private const val API_PATH = "api"
    }
}