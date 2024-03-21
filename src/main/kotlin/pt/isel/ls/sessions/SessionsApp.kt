package pt.isel.ls.sessions

import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.repository.jdbc.AppJdbcDB

const val DEFAULT_PORT = 1904
const val JDBC_DATABASE_URL_ENV = "JDBC_DATABASE_URL"

fun main() {
    val jdbcURL: String? = System.getenv(JDBC_DATABASE_URL_ENV)
    val database = jdbcURL?.let { AppJdbcDB(jdbcURL) } ?: AppMemoryDB()
    val server = AppServer(DEFAULT_PORT, database)
    server.start()
}