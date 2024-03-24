package pt.isel.ls.sessions.http.util

import kotlinx.serialization.Serializable
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri

class Problem(
    val typeUri: Uri,
    val title: String = "",
    val status: Status = Status.INTERNAL_SERVER_ERROR,
    val detail: String = "",
    val instance: Uri? = null,
) {
    private fun toResponse() = Response(status).jsonResponse(ProblemDTO.fromProblem(this), PROBLEM_TYPE)

    companion object {
        private const val PROBLEM_TYPE = "application/problem+json"
        private const val BASE_URL = "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/"

        //region Bad Request
        private val missGenreAndDeveloper = Uri.of(BASE_URL + "genres-or-developer-missing")
        private val invalidDate = Uri.of(BASE_URL + "invalid-date")
        private val invalidState = Uri.of(BASE_URL + "invalid-state")
        private val invalidCapacity = Uri.of(BASE_URL + "invalid-capacity")
        private val invalidEmail = Uri.of(BASE_URL + "invalid-email")
        private val invalidRequest = Uri.of(BASE_URL + "invalid-request")
        private val invalidGameData = Uri.of(BASE_URL + "invalid-game-data")
        private val invalidSkipOrLimit = Uri.of(BASE_URL + "invalid-skip-or-limit")
        private val genresOrDeveloperMissing = Uri.of(BASE_URL + "genres-or-developer-missing")
        //endregion

        //region Conflict, Already Exists
        private val nameAlreadyExists = Uri.of(BASE_URL + "name-already-exists")
        private val playerAlreadyInSession = Uri.of(BASE_URL + "player-already-in-session")
        private val sessionIsFull = Uri.of(BASE_URL + "session-is-full")
        private val gameNameAlreadyExists = Uri.of(BASE_URL + "game-name-already-exists")
        private val emailAlreadyExists = Uri.of(BASE_URL + "email-already-exists")
        private val internalServerError = Uri.of(BASE_URL + "internal-server-error")
        private val tokenNotFound = Uri.of(BASE_URL + "token-not-found")

        //endregion

        //region Not Found
        private val notFound = Uri.of(BASE_URL + "not-found")
        private val gameNotFound = Uri.of(BASE_URL + "game-not-found")
        private val playerNotFound = Uri.of(BASE_URL + "player-not-found")
        private val sessionNotFound = Uri.of(BASE_URL + "session-not-found")
        private val genreNotFound = Uri.of(BASE_URL + "genre-not-found")
        private val developerNotFound = Uri.of(BASE_URL + "developer-not-found")
        //endregion

        fun missingParameters(
            instance: Uri?,
            missingParams: List<String>,
        ) = Problem(
            typeUri = missGenreAndDeveloper,
            title = "Missing parameter(s)",
            status = Status.BAD_REQUEST,
            detail = "Missing parameter(s): ${missingParams.joinToString()}",
            instance = instance,
        ).toResponse()

        fun nameAlreadyExists(
            instance: Uri?,
            name: String,
        ) = Problem(
            typeUri = nameAlreadyExists,
            title = "Name already exists",
            status = Status.CONFLICT,
            detail = "Game with given name $name already exists",
        ).toResponse()

        fun gameNotFound(instance: Uri?) =
            Problem(
                typeUri = gameNotFound,
                title = "Game not found",
                status = Status.NOT_FOUND,
                detail = "Game with given id not found",
                instance = instance,
            ).toResponse()

        fun invalidDate(instance: Uri?) =
            Problem(
                typeUri = invalidDate,
                title = "Invalid date",
                status = Status.BAD_REQUEST,
                detail = "Date is invalid",
                instance = instance,
            ).toResponse()

        fun invalidState(instance: Uri?) =
            Problem(
                typeUri = invalidState,
                title = "Invalid state",
                status = Status.BAD_REQUEST,
                detail = "State is invalid",
                instance = instance,
            ).toResponse()

        fun playerNotFound(
            instance: Uri?,
            id: UInt?,
        ) = Problem(
            typeUri = playerNotFound,
            title = "Player not found",
            status = Status.NOT_FOUND,
            detail = "Player with given id: $id not found",
            instance = instance,
        ).toResponse()

        fun sessionNotFound(
            instance: Uri?,
            id: UInt,
        ) = Problem(
            typeUri = sessionNotFound,
            title = "Session not found",
            status = Status.NOT_FOUND,
            detail = "Session with given id: $id not found",
            instance = instance,
        ).toResponse()

        fun invalidCapacity(instance: Uri?) =
            Problem(
                typeUri = invalidCapacity,
                title = "Invalid capacity",
                status = Status.BAD_REQUEST,
                detail = "Capacity is invalid",
                instance = instance,
            ).toResponse()

        fun playerAlreadyInSession(instance: Uri?) =
            Problem(
                typeUri = playerAlreadyInSession,
                title = "Player already in session",
                status = Status.CONFLICT,
                detail = "Player is already in session",
                instance = instance,
            ).toResponse()

        fun sessionIsFull(instance: Uri?) =
            Problem(
                typeUri = sessionIsFull,
                title = "Session is full",
                status = Status.CONFLICT,
                detail = "Session is full",
                instance = instance,
            ).toResponse()

        fun genreNotFound(instance: Uri?) =
            Problem(
                typeUri = genreNotFound,
                title = "Genre not found",
                status = Status.NOT_FOUND,
                detail = "Genre or genres not found",
                instance = instance,
            ).toResponse()

        fun developerNotFound(instance: Uri?) =
            Problem(
                typeUri = developerNotFound,
                title = "Developer not found",
                status = Status.NOT_FOUND,
                detail = "Developer not found",
                instance = instance,
            ).toResponse()

        fun gameNameAlreadyExists(
            instance: Uri?,
            name: String,
        ) = Problem(
            typeUri = gameNameAlreadyExists,
            title = "Game name already exists",
            status = Status.CONFLICT,
            detail = "Game with given name $name already exists",
            instance = instance,
        ).toResponse()

        fun emailAlreadyExists(
            instance: Uri?,
            email: String,
        ) = Problem(
            typeUri = emailAlreadyExists,
            title = "Email already exists",
            status = Status.CONFLICT,
            detail = "Player with given email $email already exists",
            instance = instance,
        ).toResponse()

        fun invalidEmail(instance: Uri?) =
            Problem(
                typeUri = invalidEmail,
                title = "Invalid email",
                status = Status.BAD_REQUEST,
                detail = "Email is invalid",
                instance = instance,
            ).toResponse()

        fun invalidRequest(
            uri: Uri,
            detail: String = "Invalid request",
        ) = Problem(
            typeUri = invalidRequest,
            title = "Invalid Request",
            status = Status.BAD_REQUEST,
            detail = detail,
            instance = uri,
        ).toResponse()

        fun internalServerError(
            uri: Uri,
            detail: String = "Internal server error",
        ) = Problem(
            typeUri = internalServerError,
            title = "Internal Server Error",
            status = Status.INTERNAL_SERVER_ERROR,
            detail = detail,
            instance = uri,
        ).toResponse()

        fun notFound(
            uri: Uri,
            detail: String = "Not found",
        ) = Problem(
            typeUri = notFound,
            title = "Not Found",
            status = Status.NOT_FOUND,
            detail = detail,
            instance = uri,
        ).toResponse()

        fun tokenNotFound(
            uri: Uri,
            detail: String = "Token not found",
        ) = Problem(
            typeUri = tokenNotFound,
            title = "Token not found",
            status = Status.UNAUTHORIZED,
            detail = detail,
            instance = uri,
        ).toResponse()

        fun invalidGameData(
            uri: Uri,
            detail: String = "Invalid game data",
        ) = Problem(
            typeUri = invalidGameData,
            title = "Invalid game data",
            status = Status.BAD_REQUEST,
            detail = detail,
            instance = uri,
        ).toResponse()

        fun invalidSkipOrLimit(
            uri: Uri,
            detail: String = "Invalid skip or limit",
        ) = Problem(
            typeUri = invalidSkipOrLimit,
            title = "Invalid skip or limit",
            status = Status.BAD_REQUEST,
            detail = detail,
            instance = uri,
        ).toResponse()

        fun genresOrDeveloperMissing(
            uri: Uri,
            detail: String = "Genres or developer is empty",
        ) = Problem(
            typeUri = genresOrDeveloperMissing,
            title = "Genres or developer is empty",
            status = Status.BAD_REQUEST,
            detail = detail,
            instance = uri,
        ).toResponse()
    }
}

@Serializable
data class ProblemDTO(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String?,
) {
    companion object {
        fun fromProblem(problem: Problem) =
            ProblemDTO(
                problem.typeUri.toString(),
                problem.title,
                problem.status.code,
                problem.detail,
                problem.instance?.toString(),
            )
    }
}
