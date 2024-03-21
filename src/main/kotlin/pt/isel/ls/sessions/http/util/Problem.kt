package pt.isel.ls.sessions.http.util

import org.http4k.core.Response
import org.http4k.core.Status
import java.net.URI

class Problem(
    val typeUri: URI,
    val title: String = "",
    val status: Status = Status.INTERNAL_SERVER_ERROR,
    val detail: String = "",
    val instance: URI? = null,
) {
    private fun toResponse() = Response(status).jsonResponse(this, PROBLEM_TYPE)

    companion object {
        private const val PROBLEM_TYPE = "application/problem+json"
        private const val BASE_URL = "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/"

        //region Bad Request
        private val missGenreAndDeveloper = URI(BASE_URL + "genres-or-developer-missing")
        //endregion

        //region Conflict, Already Exists
        private val nameAlreadyExists = URI(BASE_URL + "name-already-exists")

        //endregion

        //region Not Found
        private val gameNotFound = URI(BASE_URL + "game-not-found")
        //endregion

        fun missingParameters(instance: URI?) =
            Problem(
                typeUri = missGenreAndDeveloper,
                title = "Problem.missingParameter",
                status = Status.BAD_REQUEST,
                detail = "Genres or developer are missing",
                instance = instance,
            ).toResponse()

        fun nameAlreadyExists(instance: URI?) =
            Problem(
                typeUri = nameAlreadyExists,
                title = "Problem.nameAlreadyExists",
                status = Status.CONFLICT,
                detail = "Game with given name already exists",
            ).toResponse()

        fun gameNotFound(instance: URI?) =
            Problem(
                typeUri = gameNotFound,
                title = "Problem.gameNotFound",
                status = Status.NOT_FOUND,
                detail = "Game with given id not found",
                instance = instance,
            ).toResponse()
    }
}
