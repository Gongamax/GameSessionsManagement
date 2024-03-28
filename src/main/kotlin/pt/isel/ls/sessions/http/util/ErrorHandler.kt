package pt.isel.ls.sessions.http.util

import kotlinx.serialization.SerializationException
import org.http4k.core.Request
import org.http4k.core.Response
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.routes.utils.TokenNotFoundException
import pt.isel.ls.sessions.repository.jdbc.ExecuteSqlException
import java.sql.SQLException
import java.time.DateTimeException

private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.util.ErrorHandler")

inline fun execStart(
    request: Request,
    block: () -> Response,
): Response =
    try {
        logRequest(request)
        block()
    } catch (error: SerializationException) {
        Problem.invalidRequest(request.uri)
    } catch (error: ExecuteSqlException) {
        Problem.internalServerError(request.uri, error.errorInfo)
    } catch (e: SQLException) {
        Problem.internalServerError(request.uri, "SQL internal error")
    } catch (error: NumberFormatException) {
        Problem.invalidRequest(request.uri, "Invalid number format")
    } catch (error: TokenNotFoundException) {
        Problem.tokenNotFound(request.uri, "Unauthorized, token not found")
    } catch (error: IllegalArgumentException) {
        Problem.invalidRequest(request.uri, error.message ?: "Invalid request")
    } catch (error: NoSuchElementException) {
        Problem.notFound(request.uri)
    } catch (error: DateTimeException) {
        Problem.invalidDate(request.uri)
    } catch (error: IllegalStateException) {
        Problem.invalidRequest(request.uri)
    } catch (error: Exception) {
        Problem.internalServerError(request.uri, "Internal server error")
    }

fun logRequest(request: Request) {
    logger.info(
        "incoming request: method={}, uri={}, content-type={}, accept={}, body={}",
        request.method,
        request.uri,
        request.header(CONTENT_TYPE),
        request.header(ACCEPT),
        request.bodyString(),
    )
}
