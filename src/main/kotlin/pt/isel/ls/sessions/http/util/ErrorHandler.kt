package pt.isel.ls.sessions.http.util

import kotlinx.serialization.SerializationException
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.repository.jdbc.ExecuteSqlException
import java.sql.SQLException

private val logger = LoggerFactory.getLogger("pt.isel.ls.sessions.http.util.ErrorHandler")

// TODO: Improve error handling
inline fun execStart(
    request: Request,
    block: () -> Response,
): Response =
    try {
        logRequest(request)
        block()
    } catch (error: SerializationException) {
        Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Invalid request"))
    } catch (error: ExecuteSqlException) {
        Response(Status.INTERNAL_SERVER_ERROR).jsonResponse(MessageResponse(error.errorInfo))
    } catch (e: SQLException) {
        Response(Status.INTERNAL_SERVER_ERROR).jsonResponse(MessageResponse("SQL internal error"))
    } catch (error: NumberFormatException) {
        Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Invalid request, invalid number format"))
    } catch (error: IllegalArgumentException) {
        Response(Status.BAD_REQUEST).jsonResponse(MessageResponse(error.message ?: "Invalid request"))
    } catch (error: NoSuchElementException) {
        Response(Status.NOT_FOUND).jsonResponse(MessageResponse("Not found"))
    } catch (error: IllegalStateException) {
        Response(Status.BAD_REQUEST).jsonResponse(MessageResponse("Invalid request"))
    } catch (error: Exception) {
        Response(Status.INTERNAL_SERVER_ERROR).jsonResponse(MessageResponse("Internal server error"))
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
