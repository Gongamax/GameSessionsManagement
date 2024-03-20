package pt.isel.ls.sessions

import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.AppWebApi
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.AppService

const val DEFAULT_PORT = 1904

class AppServer(private val port: Int, private val database: AppMemoryDB) {
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

//TODO: Move this to a main file
fun main() {
    val database = AppMemoryDB()
    val server = AppServer(DEFAULT_PORT, database)
    server.start()
}